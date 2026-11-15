# Build GameSidekick using Cloud APK Builder

1. Extract the ZIP file.
2. In Cloud APK Builder, choose the extracted `GameSidekick` project folder (the folder containing `settings.gradle.kts`).
3. Run **Preflight Check**. The project now includes `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.properties`.
4. If preflight passes, tap **Build Project**.

The project is a native Kotlin Android app, not a Flutter app. It does not require `pubspec.yaml` or Dart source files.

## Build configuration
- Android Gradle Plugin: 8.7.3
- Kotlin Android plugin: 2.0.21
- Gradle distribution: 8.9
- Compile SDK: 35
- Minimum SDK: 26
- Target SDK: 35

The included `gradlew` launchers use an installed Gradle if available; otherwise they download Gradle 8.9. This project ZIP has not been built or tested inside Cloud APK Builder, so a successful preflight does not guarantee the APK build will succeed.
