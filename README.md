# NoScrollTill 💜

**Code first. Scroll later.**

NoScrollTill is an Android productivity app that helps reduce mindless Instagram scrolling by encouraging you to solve a LeetCode problem first.

The idea is simple: **Complete a LeetCode problem, unlock Instagram, and build a daily coding habit.**

##  Features

- **Instagram blocking** — Restricts access to Instagram until your daily coding goal is completed.
- **LeetCode integration** — Checks your recent accepted submissions.
- **Daily unlock** — Unlocks Instagram after detecting an accepted LeetCode submission for the current daily cycle.
- **4 PM reset** — Starts a new daily cycle at 4:00 PM local device time.
- **Countdown timer** — Displays the time remaining until the next reset.
- **Minimal interface** — A clean, purple-themed productivity dashboard.
- **Streak display** — Includes a streak section for tracking coding consistency (tracking logic is still under development).

##  How It Works

1. Open NoScrollTill.
2. Enable its Accessibility Service in your Android settings.
3. Instagram remains restricted during a locked daily cycle.
4. Tap **Open LeetCode** and solve a coding problem.
5. Submit your solution and receive an **Accepted** result.
6. Return to NoScrollTill and tap **Check my submission**.
7. Once the app verifies an accepted submission from the current cycle, Instagram becomes accessible.
8. At 4:00 PM, a new daily cycle begins.

##  Tech Stack

- **Language:** Java
- **IDE:** Android Studio
- **UI:** Android XML layouts
- **Build system:** Gradle
- **Storage:** SharedPreferences
- **Accessibility:** Android Accessibility Service
- **LeetCode integration:** Unofficial Alfa LeetCode API

##  Getting Started

### Prerequisites

- Android Studio
- Android SDK
- An Android device or emulator
- Internet connection
- A LeetCode account

### Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/keerthanaradhakrishnan1111-gif/NoScrollTill.git
   ```

2. Open the project in Android Studio.
3. Wait for Gradle synchronization to finish.
4. Connect an Android device or start an emulator.
5. Build and run the app.
6. Enable NoScrollTill under your device's Accessibility settings.

**Note:** The current prototype uses a hardcoded LeetCode username. To use your own account, update the username constant in `MainActivity.java` before building.

##  Current Limitations

- The app currently supports Instagram blocking only.
- The LeetCode username is not yet configurable through the app interface.
- The unofficial LeetCode API may occasionally be slow or unavailable.
- Accessibility-based blocking can behave differently across Android devices and may be bypassed by disabling the service.
- Full automatic streak tracking is not yet implemented.

##  Future Improvements

- Allow users to enter their LeetCode username.
- Implement daily coding streak tracking.
- Improve background reliability.
- Add support for more distracting apps.
- Improve error handling and the overall user experience.

##  Privacy

NoScrollTill uses Android's Accessibility Service to detect when Instagram is opened and enforce its blocking behavior.

The app checks publicly available LeetCode submission information using an unofficial API. It does not require your LeetCode password.

##  Contributions

Contributions, bug reports, and suggestions are welcome! Feel free to open an issue or submit a pull request.

##  Project Status

**Early development / personal learning project**

NoScrollTill is being built and improved as a practical Android development and productivity project.

---

Made with 💜 to turn scrolling time into coding time.
