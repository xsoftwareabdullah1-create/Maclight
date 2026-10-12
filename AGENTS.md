# Codex Instructions — WinLite10

## User's goal
Build WinLite10 into a real Android 10+ Windows-compatibility app inspired by Winlator, Boxvidra, and XoDos. Do not stop at a Windows-themed launcher UI. The user has explicitly chosen Android 10+ as the minimum supported Android version.

## Non-negotiable requirements
- Set and maintain minSdk 29 (Android 10). Do not retain Android 8.1/API 27 compatibility as a project requirement.
- Preserve the existing GitHub Actions APK build and fix regressions rather than replacing it with a mockup.
- Prioritize a technically real Windows-app runtime path: compatible Linux userspace, Wine, CPU translation (ARM64/Box64 and any required 32-bit support), graphics/Vulkan compatibility, container setup, C: drive mapping, and process/output/error handling.
- Never claim that EXE/MSI files run until an actual runtime is integrated and tested. A button or status screen is not an emulator.
- Inspect upstream project licenses, Android SDK/minSdk requirements, supported ABIs, and redistribution requirements before copying or bundling components. Prefer a maintainable integration strategy over attempting to reimplement Wine/Box64.
- The app must explain runtime download/storage requirements, supported device limits, and unsupported apps clearly.
- Keep implementation compatible with the current Kotlin/Gradle project and GitHub Actions. Make incremental changes, run tests/build where possible, and report exact failures honestly.
- First inspect the existing repository and CI results; then implement a concrete Android 10+ compatibility milestone instead of asking the user to reconfirm the Android 10+ choice.
