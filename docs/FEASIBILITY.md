# Feasibility and implementation notes

## What is implemented now

This repository is milestone 1: an Android-native shell and private workspace. Its buttons are connected to implemented actions for navigation, settings, folder creation, file import, file details, deletion, and device diagnostics.

## What is not implemented

- Wine runtime and prefix/container management.
- Linux userspace, process launcher, display server/compositor, audio, or mouse/input bridge for Wine.
- Box86/Box64 or alternative CPU translation.
- Windows executable execution.
- Windows C: drive mapping.
- Chrome/Chromium desktop application.
- Desktop VS Code, Claude Code, Codex desktop clients, or other Windows AI agents.
- Workspace archive export/import, cloud sync, or crash report upload.

## Why CPU verification comes first

The Redmi Note 5 name can refer to different regional variants and SoCs. The app Diagnostics screen reports SUPPORTED_ABIS, Android API, and memory. Collect these results from the actual device before selecting native runtime binaries. A 32-bit ARM userspace, ARM64 userspace, and x86/x64 translation approach are not interchangeable.

## Acceptance gate for the next milestone

Before claiming Windows compatibility, the project must bundle or provision a legally redistributable runtime, launch it on the target Android version, run a known compatible EXE, retain its logs, and test failure handling. Until that test is completed, the compatibility status must remain "not integrated".
