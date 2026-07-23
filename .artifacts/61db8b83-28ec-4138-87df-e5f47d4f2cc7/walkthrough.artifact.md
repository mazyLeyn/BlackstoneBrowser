# Walkthrough - Renamed App to "Warden Browser"

I have successfully renamed the application from "Blackstone Browser" to "Warden Browser" throughout the entire project.

## Changes Made

### Strings and Resources
#### [strings.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/values/strings.xml)
- Updated `app_name` to "Warden Browser".
- Updated `brand_name_part1` to "Warden".

#### [activity_about.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/layout/activity_about.xml)
- Updated the header text to use `@string/app_name`, correctly displaying "Warden Browser".

#### [colors.xml](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/res/values/colors.xml)
- Updated comments to reflect the new "Warden Browser" branding.

### Project Configuration
#### [settings.gradle.kts](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/settings.gradle.kts)
- Changed the root project name to `WardenBrowser`.

### Code and Links
#### [AboutActivity.kt](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/java/com/example/mybrowser/AboutActivity.kt) and [SettingsActivity.kt](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/src/main/java/com/example/mybrowser/SettingsActivity.kt)
- Updated GitHub repository links to `https://github.com/mazyLeyn/WardenBrowser` for consistency.

### Documentation
#### [README.md](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/README.md)
- Replaced all 12 occurrences of "Blackstone Browser" with "Warden Browser".
- Updated clone and release links to point to the new repository name.

### Version Update
#### [build.gradle.kts](file:///C:/Users/Onur/Desktop/BlackstoneBrowser%20v1.3.6/MyBrowser/app/build.gradle.kts)
- Updated `versionCode` to `10`.
- Updated `versionName` to `"1.3.9"`.

## Verification Result

### Build Status
- Ran `./gradlew :app:assembleDebug` and the build finished **successfully**.

### Visual Verification
- The app name in the launcher and all internal screens will now reflect "Warden Browser".
- Links point to the updated repository URL.
- App version correctly displays as v1.3.9.
