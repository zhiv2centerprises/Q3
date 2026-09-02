# Build Q3 into an APK with GitHub Actions

1. Create a GitHub repository.
2. Upload the contents of this project to the repository root (do not create an extra Q3 folder inside it).
3. Make sure the default branch is `main`.
4. Open **Actions** and select **Q3 Android APK**.
5. Click **Run workflow**.
6. Wait for the build to finish.
7. Open the completed workflow run and download the **Q3-debug-apk** artifact.
8. Extract the artifact and install the APK on an Android device.

This workflow uses Java 17 and Gradle 9.1.0, matching the Android Gradle Plugin 9.0.1 compatibility requirements.

The debug APK is intended for testing. A Play Store release requires a properly signed release build with your own private signing key.
