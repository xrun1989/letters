# Grammar Studio — v0.1

A small native Android grammar-writing prototype built with Kotlin and Jetpack Compose.

## Current prototype
- Editable writing area.
- Clickable word chips generated from the text.
- Lower analysis panel with basic heuristic explanations and correction suggestions.
- Warm paper / ink visual direction.
- GitHub Actions workflow to build a debug APK on push or manual run.
- Separate AI, app-release, and token-estimate logs.

## Build locally
Open this project in Android Studio (JDK 17 recommended), allow Gradle sync, then run the `app` configuration.

Command line:
```bash
./gradlew assembleDebug
```
The APK is normally written to `app/build/outputs/apk/debug/app-debug.apk`.

## GitHub APK build
Push the repository to GitHub. The workflow `.github/workflows/android.yml` builds the debug APK and uploads it as an Actions artifact. It does not publish a release automatically.

## Versioning
For the initial release:
```bash
git add .
git commit -m "Create Grammar Studio v0.1 prototype"
git tag v0.1
git push origin main --tags
```
For later versions, update `CHANGELOG.md`, bump `versionName` in `app/build.gradle.kts`, commit, and create the next tag. Git tags are explicit so accidental pushes do not silently create releases.

## Important limits in v0.1
The analysis is a local rule-based teaching prototype, not a full grammar engine. Dictionary downloads/imports, robust grammar checking, word-class tagging, and the full theme selector are not implemented yet.
