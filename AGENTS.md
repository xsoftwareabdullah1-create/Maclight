# Codex Instructions — MacLite (repository: Winlite10)

## User's latest product direction
Transform the app from a Windows-inspired shell into a polished **macOS-inspired desktop experience for Android 10+**. Use an original Mac-like design language: top menu bar, centered/translucent dock, Finder-like file browser, rounded panels, refined typography, wallpaper, desktop icons, and smooth interactions. Do not use Apple's logos or claim this is actual macOS.

## Product truth and runtime
- This is an Android app, not a macOS emulator or Apple operating system.
- Keep Android minimum API 29 (Android 10+) and preserve the GitHub Actions APK build.
- The user wants a polished desktop experience and real app compatibility, not a static mockup. Do not imply that macOS apps or Windows EXE files execute unless an actual compatible runtime is integrated and tested.
- Windows EXE compatibility via Wine/CPU translation is a separate backend; it does not come from Mac-style UI. Native macOS app execution on Android is not a realistic feature to promise.
- Inspect current repository and CI results first. Make incremental changes, run tests/builds, fix regressions, and report exact results honestly.
- Check third-party licenses, SDK requirements, ABIs, and redistribution rules before integrating external runtime code or binaries.
- Do not ask the user to reconfirm the Mac-style UI direction; they explicitly requested this change.

## UI acceptance criteria
- Replace Windows 11 Start menu/taskbar/branding with a macOS-inspired top menu bar and dock.
- Create a Finder-inspired file manager and Mac-like settings/about panels.
- Make controls functional within the Android prototype; don't present non-working EXE launch as implemented.
- Keep layouts responsive on phone portrait and landscape, and retain diagnostics and private file workspace.
