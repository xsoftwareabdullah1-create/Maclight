<div align="center">

<img src="assets/maclite-banner.svg" alt="MacLite — macOS-inspired Android desktop" width="100%"/>

# MacLite

### A Mac-inspired desktop experience for Android

A lightweight Android desktop-shell prototype with a clean workspace, file tools, settings, and device diagnostics.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Minimum SDK](https://img.shields.io/badge/minimum%20Android-10%20(API%2029)-64748b)
![Build](https://img.shields.io/badge/build-GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-blue)

[Features](#features) · [Install](#install) · [Build from source](#build-from-source) · [Roadmap](#roadmap) · [Limitations](#limitations)

</div>

---

## Overview

MacLite is an Android-native project exploring a macOS-inspired desktop layout on a phone. It is a **desktop-style interface prototype**, not a macOS virtual machine and not a Windows emulator.

The project focuses on a polished desktop shell, a private file workspace, simple preferences, and useful device information. The app's current Android minimum is **Android 10 (API 29)**. Android 8 devices are not supported by the current build configuration.

## Features

| Feature | Description |
|---|---|
| 🖥️ Desktop workspace | Mac-inspired desktop layout and app-launching surfaces |
| 📁 Private file workspace | Browse app-private workspace items and inspect file details |
| 📥 Import files | Import selected files using Android's document picker |
| 📂 File and folder actions | Create folders and delete workspace items with confirmation |
| ⚙️ Appearance settings | Switch between light and dark themes |
| 📱 Device diagnostics | Inspect Android version, device model, CPU ABIs, and memory information |
| 🧪 Compatibility status | See which desktop/runtime capabilities are not yet integrated |
| ☁️ Automated build | GitHub Actions can run tests and produce a debug APK artifact when the build succeeds |

## Install

### Download a build artifact

1. Open the [MacLite GitHub Actions runs](https://github.com/xsoftwareabdullah1-create/Maclight/actions/workflows/android.yml).
2. Choose the latest run marked **successful**.
3. In the run's **Artifacts** section, download the APK artifact.
4. Extract the downloaded ZIP file if necessary.
5. Locate the actual `.apk` file and install that file—not the ZIP itself.

Only use an artifact from a successful run. If Android reports **“There was a problem parsing the package,”** confirm that you extracted the ZIP, downloaded the APK completely, and that the device meets the minimum Android version.

> **Compatibility note:** The current project requires Android 10 (API 29) or newer. Android 8 / 8.1 cannot install a build with this minimum SDK. Lowering the minimum SDK alone does not guarantee compatibility; the app and its dependencies must also be checked and tested on the older Android version.

## Build from source

### Requirements

- JDK 17
- Android SDK platform 35
- Gradle 8.9 (CI uses the project's configured Gradle setup)

Run the project's Gradle wrapper from the repository root:

```bash
./gradlew --no-daemon testDebugUnitTest
./gradlew --no-daemon assembleDebug
```

On a successful build, the debug APK is normally written to:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Roadmap

- [x] Android-native desktop-style shell
- [x] Private file workspace and basic file/folder actions
- [x] Theme settings and device diagnostics
- [x] GitHub Actions build workflow
- [ ] Refine compact-phone, tablet, and landscape layouts
- [ ] Add workspace export and backup
- [ ] Evaluate an optional Linux userspace and compatible application runtime
- [ ] Validate runtime compatibility on supported Android devices

## Current limitations

- **This is not macOS.** It provides a Mac-inspired Android interface; it does not boot macOS or run Mac applications.
- **Windows EXE/MSI execution is not implemented.** A compatibility/status screen is not an executable runtime.
- Wine, Linux userspace, CPU translation, and a Windows desktop runtime are not bundled in the current milestone.
- The workspace uses private app storage. Uninstalling the app or clearing its data may remove imported files. Keep backups of important files.
- Build artifacts are only usable when the CI build completes successfully.

## Crash diagnostics

If the app closes unexpectedly, capture the Android crash dialog's summary or the relevant `FATAL EXCEPTION` stack trace from Logcat. Include the device model, Android version, and steps to reproduce. Do not share passwords, API keys, or other secrets.

## Technology

- Kotlin and native Android UI
- Android Gradle Plugin 8.7.3
- Gradle 8.9
- JDK 17
- GitHub Actions

## Contributing

Bug reports and pull requests are welcome. Please include device details, Android version, reproduction steps, and relevant build logs.

## License

Released under the [MIT License](LICENSE).

---

<div align="center">

**MacLite — a Mac-inspired desktop concept, built natively for Android.**

[Repository](https://github.com/xsoftwareabdullah1-create/Maclight) · [Builds and APK artifacts](https://github.com/xsoftwareabdullah1-create/Maclight/actions) · [Report an issue](https://github.com/xsoftwareabdullah1-create/Maclight/issues)

</div>
