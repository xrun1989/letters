# Grammar Studio v0.1
Native Android prototype using Kotlin and Jetpack Compose.

## Included
- Editable manuscript area.
- Compact contextual word strip.
- Bottom analysis desk with Sentence, Craft & Style, Authors, Techniques, Structure, Plot, and Wiki-Lens tabs.
- Settings for Lens mode, writing/grammar level, English variety, and visibility.
- GitHub Actions workflow to build a debug APK artifact.

## Build
Open in Android Studio with JDK 17 and allow Gradle sync, or push to GitHub and run the Android APK workflow. The workflow uploads `app/build/outputs/apk/debug/app-debug.apk`.

## Limits
This is a UI prototype. Grammar checks are heuristic; full grammar analysis, dictionary downloads, saved documents, real halftone rendering, and interconnected Wiki-Lens articles remain future work.
