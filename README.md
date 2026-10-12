# WinLite 10

WinLite 10 is a native Android prototype for a lightweight Windows-style workspace. The first milestone provides a desktop-like shell and file workspace, but **does not yet run Windows EXE/MSI files**.

## Target and build

- Target device: Redmi Note 5, Android 8.1 (API 27), subject to verification on the actual device.
- Android minimum API: 27.
- Android Gradle Plugin 8.7.3, Gradle 8.9, JDK 17, compile SDK 35.
- GitHub Actions workflow: `.github/workflows/android.yml`.
- Package: `com.winlite10`; CI artifact: `WinLite10.apk`.

## Features

- Native dark-blue desktop-style home screen, Start menu and taskbar-style navigation.
- File Explorer for the app-private workspace.
- Create folders, import files from Android's document picker, view metadata and delete workspace items with confirmation.
- EXE/MSI file selection with a notice that execution is not implemented.
- Dark/light appearance toggle and device diagnostics.

## Important limitations

This is **not Windows 10**. This milestone does not include Wine, a Linux userspace, Box86/Box64 or another CPU translation layer. It cannot execute Windows programs. Selecting an EXE/MSI only selects the file and displays a limitation notice.

The app's workspace is in private app storage. Uninstalling the app or clearing its data normally deletes that data. Export/backup tooling is not included. Do not store your only copy of important files here.

## Build with GitHub Actions

1. Open **Actions** in the repository and enable Actions if prompted.
2. Select **Build WinLite 10 Android APK** and click **Run workflow**, or push a commit to `main`.
3. Wait for the workflow to finish.
4. Open the completed run and download the `WinLite10-APK` artifact.
5. Extract the artifact ZIP; it contains `WinLite10.apk` and `SHA256SUMS.txt`.

The workflow builds a debug APK for testing, not a signed Play Store release. The project has not been verified on the target phone yet.

## Next steps

Verify the actual device's CPU ABI and available RAM, then evaluate a legally redistributable Wine/userspace and translation layer before claiming Windows application compatibility.
