
package com.example.noscrolltill;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityEvent;

import java.util.Calendar;

public class NoScrollAccessibilityService
        extends AccessibilityService {

    private static final String PREFS = "NoScrollTillData";
    private static final String KEY_UNLOCKED = "unlocked";
    private static final String KEY_CYCLE_START = "cycleStart";

    private long getCycleStartMillis() {
        Calendar now = Calendar.getInstance();
        Calendar reset = (Calendar) now.clone();

        reset.set(Calendar.HOUR_OF_DAY, 16);
        reset.set(Calendar.MINUTE, 0);
        reset.set(Calendar.SECOND, 0);
        reset.set(Calendar.MILLISECOND, 0);

        if (now.before(reset)) {
            reset.add(Calendar.DAY_OF_MONTH, -1);
        }

        return reset.getTimeInMillis();
    }

    private boolean isUnlocked() {
        SharedPreferences prefs =
                getSharedPreferences(PREFS, MODE_PRIVATE);

        return prefs.getBoolean(KEY_UNLOCKED, false)
                && prefs.getLong(KEY_CYCLE_START, -1L)
                == getCycleStartMillis();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null
                || event.getEventType()
                != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || event.getPackageName() == null) {
            return;
        }

        String packageName = event.getPackageName().toString();

        if ("com.instagram.android".equals(packageName)
                && !isUnlocked()) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
            );

            startActivity(intent);
        }
    }

    @Override
    public void onInterrupt() {
    }
}
