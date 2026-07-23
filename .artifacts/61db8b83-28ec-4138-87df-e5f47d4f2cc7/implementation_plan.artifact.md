# Implementation Plan - Rename App to "Warden Browser"

This plan covers renaming the application from "Blackstone Browser" to "Warden Browser" across all user-facing strings, layouts, and project configurations.

## Proposed Changes

### Strings and Resources

#### [MODIFY] [strings.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/values/strings.xml)
- Change `app_name` from "Blackstone Browser" to "Warden Browser".
- Change `brand_name_part1` from "Blackstone" to "Warden".

#### [MODIFY] [activity_about.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/layout/activity_about.xml)
- Update the hardcoded "Blackstone Browser" text to "Warden Browser".

#### [MODIFY] [colors.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/values/colors.xml)
- Update the comment mentioning "Blackstone Browser".

### Project Configuration

#### [MODIFY] [settings.gradle.kts](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/settings.gradle.kts)
- Change `rootProject.name` from "BlackstoneBrowser" to "WardenBrowser".

### Documentation and Metadata

#### [MODIFY] [README.md](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/README.md)
- Replace all occurrences of "Blackstone Browser" with "Warden Browser".

#### [MODIFY] [AboutActivity.kt](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/java/com/example/mybrowser/AboutActivity.kt)
- Update the GitHub link text if desired (optional, but consistent). I'll wait for user input on whether the GitHub repo name is also changing.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure the project name change in `settings.gradle.kts` doesn't break the build.

### Manual Verification
- [ ] Check the app icon label on the device/emulator home screen (should be "Warden Browser").
- [ ] Check the "About" screen in the app (should show "Warden Browser").
- [ ] Check the Home screen brand logo (should show "Warden Browser").
