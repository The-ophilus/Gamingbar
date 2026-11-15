# Game Sidekick (Android)

A small Android floating edge tab with an installed-app shortcut list. Intended for sideloading on Android 8+ and tested by design for Android 15 / HiOS, but **not device-tested**.

## What it does
- Requests Android's "Display over other apps" permission.
- Runs a foreground service so the edge tab can remain above a game.
- Lets the user drag the tab and open/close a shortcut panel.
- Lists installed launchable apps and launches the selected app.

## Important limitation
This app does not embed other apps or force them into freeform windows. Android/HiOS controls whether an app can appear in a resizable pop-up while a game remains visible. If HiOS does not provide a floating-window feature for the selected app, launching it can switch away from the game. The sidebar itself can remain as an overlay, but this is not a replacement for OS-level multi-window support.

## Build an APK
1. Install Android Studio on a computer with Android SDK Platform 35 and JDK 17 or newer.
2. Open this `GameSidekick` folder in Android Studio.
3. Allow Gradle sync to complete.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Android Studio will show the generated APK location (normally `app/build/outputs/apk/debug/app-debug.apk`).
6. Copy the APK to the Tecno Spark 40, install it, open Game Sidekick, grant **Display over other apps**, and tap **Start gaming sidebar**.

For a release APK, configure signing in **Build > Generate Signed Bundle / APK**.

## Permissions
- `SYSTEM_ALERT_WINDOW`: required for the floating edge tab and shortcut panel.
- `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_SPECIAL_USE`: keep the user-started sidebar service active.
- `QUERY_ALL_PACKAGES`: list installed launchable apps. This is suitable for a privately sideloaded utility; Google Play distribution would require policy review.
- `POST_NOTIFICATIONS`: declared for Android 13+ notification behavior. The service notification is required for foreground-service operation.
