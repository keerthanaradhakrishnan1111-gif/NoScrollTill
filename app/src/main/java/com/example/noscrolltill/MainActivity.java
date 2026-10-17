
package com.example.noscrolltill;

import android.os.Handler;
import android.os.Looper;
import java.util.Locale;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private static final String USERNAME = "keerthana1403";
    private static final String PREFS = "NoScrollTillData";
    private static final String KEY_UNLOCKED = "unlocked";
    private static final String KEY_CYCLE_START = "cycleStart";

    private TextView txtStatus;
    private Button btnLeetCode;
    private Button btnComplete;
    private TextView txtCountdown;
    private TextView txtStreak;

    private final Handler countdownHandler =
            new Handler(Looper.getMainLooper());

    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            updateCountdown();
            countdownHandler.postDelayed(this, 1000);
        }
    };

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        btnLeetCode = findViewById(R.id.btnLeetCode);
        btnComplete = findViewById(R.id.btnComplete);
        txtCountdown = findViewById(R.id.txtCountdown);
        txtStreak = findViewById(R.id.txtStreak);

        txtStreak.setText(
                getSharedPreferences("NoScrollTillData", MODE_PRIVATE)
                        .getInt("streak", 0) + " days"
        );

        countdownHandler.post(countdownRunnable);

        btnLeetCode.setText("Open LeetCode");
        btnComplete.setText("Check my submission");

        btnLeetCode.setOnClickListener(v -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(
                                    "https://leetcode.com/"));
            startActivity(intent);
        });

        // This button verifies submissions; it does not unlock manually.
        btnComplete.setOnClickListener(v -> checkSubmission());

        checkSubmission();
    }

    private long getCycleStartMillis() {
        Calendar now = Calendar.getInstance();
        Calendar reset = (Calendar) now.clone();

        reset.set(Calendar.HOUR_OF_DAY, 16);
        reset.set(Calendar.MINUTE, 0);
        reset.set(Calendar.SECOND, 0);
        reset.set(Calendar.MILLISECOND, 0);

        // Before 4 PM, the current cycle began yesterday at 4 PM.
        if (now.before(reset)) {
            reset.add(Calendar.DAY_OF_MONTH, -1);
        }

        return reset.getTimeInMillis();
    }
    private boolean isUnlockedForCurrentCycle() {
        android.content.SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        return prefs.getBoolean(KEY_UNLOCKED, false)
                && prefs.getLong(KEY_CYCLE_START, -1L)
                == getCycleStartMillis();
    }
    private void updateCountdown() {
        Calendar now = Calendar.getInstance();
        Calendar nextReset = (Calendar) now.clone();

        nextReset.set(Calendar.HOUR_OF_DAY, 16);
        nextReset.set(Calendar.MINUTE, 0);
        nextReset.set(Calendar.SECOND, 0);
        nextReset.set(Calendar.MILLISECOND, 0);

        if (!now.before(nextReset)) {
            nextReset.add(Calendar.DAY_OF_MONTH, 1);
        }

        long remaining =
                nextReset.getTimeInMillis() - now.getTimeInMillis();

        long hours = remaining / 3600000;
        long minutes = (remaining % 3600000) / 60000;
        long seconds = (remaining % 60000) / 1000;

        txtCountdown.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d:%02d",
                        hours, minutes, seconds
                )
        );
    }

    private void checkSubmission() {
        txtStatus.setText("Checking your LeetCode submissions...");
        btnComplete.setEnabled(false);

        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        "https://alfa-leetcode-api.onrender.com/"
                                + USERNAME
                                + "/acSubmission?limit=50");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception(
                            "Submission service returned HTTP "
                                    + responseCode);
                }

                java.io.InputStream input =
                        connection.getInputStream();

                java.io.ByteArrayOutputStream output =
                        new java.io.ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int length;

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }

                input.close();

                JSONObject result = new JSONObject(
                        output.toString("UTF-8"));

                JSONArray submissions =
                        result.getJSONArray("submission");

                long cycleStart = getCycleStartMillis();
                boolean acceptedThisCycle = false;

                for (int i = 0; i < submissions.length(); i++) {
                    JSONObject submission =
                            submissions.getJSONObject(i);

                    if (!"Accepted".equalsIgnoreCase(
                            submission.optString("statusDisplay"))) {
                        continue;
                    }

                    long timestamp = Long.parseLong(
                            submission.getString("timestamp"));

                    // API timestamps are Unix seconds.
                    if (timestamp * 1000L >= cycleStart) {
                        acceptedThisCycle = true;
                        break;
                    }
                }

                boolean unlocked = acceptedThisCycle
                        || isUnlockedForCurrentCycle();

                if (unlocked) {
                    getSharedPreferences(PREFS, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_UNLOCKED, true)
                            .putLong(KEY_CYCLE_START, cycleStart)
                            .apply();
                }

                runOnUiThread(() -> {
                    txtStatus.setText(unlocked ? "UNLOCKED" : "LOCKED");

                    btnComplete.setEnabled(true);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    txtStatus.setText(
                            isUnlockedForCurrentCycle() ? "UNLOCKED" : "LOCKED"
                    );
                    btnComplete.setEnabled(true);

                    Toast.makeText(
                            MainActivity.this,
                            "Verification failed. Instagram "
                                    + "should remain locked.",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
        countdownHandler.removeCallbacks(countdownRunnable);
    }
}
