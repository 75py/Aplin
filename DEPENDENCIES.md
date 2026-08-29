# Dependencies

This document provides an overview of all Gradle dependencies used in the Aplin project and their purpose within the application.

## SDK Configuration

- **Gradle Wrapper**: 8.14.5 - Build automation tool version
- **Compile SDK**: 36 - Target SDK version for compilation
- **Min SDK**: 26 - Minimum Android API level supported (Android 8.0)
- **Target SDK**: 36 - Target Android API level (Android 16)
- **Java Version**: 17 - Java language version used for compilation

## Build Plugins

### Core Android Plugins
- **Android Gradle Plugin** (8.13.2) - Essential plugin for Android app development, handles Android-specific build tasks
- **Kotlin Android Plugin** (2.3.21) - Enables Kotlin language support in Android projects
- **Kotlin Compose Plugin** (2.3.21) - Provides Kotlin compiler support for Jetpack Compose

### Code Quality & Analysis
- **ktlint** (14.2.0) - Kotlin linter and formatter for consistent code style
- **Kover** (0.9.9) - Code coverage tool for Kotlin projects

### License Management
- **Cash App Licensee Gradle plugin** (1.14.1) - Validates dependency licenses and bundles a reproducible build-time `artifacts.json` report; no Licensee runtime library is shipped

## Core Libraries

### Android Core
- **AndroidX Core KTX** (1.18.0) - Kotlin extensions for Android core APIs, providing more concise and idiomatic code
- **AppCompat** (1.7.1) - Backward compatibility library and theme support used by the application

### Jetpack Compose UI Framework
- **Compose UI** (1.11.4) - Core Jetpack Compose library for building native Android UI
- **Compose Material** (1.11.4) - Material Design components for Compose
- **Compose Material Icons Core** (1.7.8) - Core Material icon set used by the app; this artifact line is separately versioned because newer Compose releases no longer publish Material Icons updates
- **Compose UI Tooling Preview** (1.11.4) - Preview support for Compose in Android Studio
- **Compose UI Tooling** (1.11.4) - Debug tooling for Compose (debug builds only)

### Lifecycle & Activity
- **Lifecycle Runtime KTX** (2.10.0) - Lifecycle-aware components with Kotlin extensions
- **Activity Compose** (1.13.0) - Integration between Activities and Jetpack Compose

### Navigation
- **Navigation Compose** (2.9.8) - Navigation component for Jetpack Compose, handles in-app navigation

### Dependency Injection
- **Koin Android** (4.2.2) - Lightweight dependency injection framework for Android

### Data & Preferences
- **DataStore Preferences** (1.2.1) - Modern replacement for SharedPreferences with type safety and coroutine support

### Utilities
- **Kotlin Reflect** (2.3.21) - Kotlin reflection library for runtime introspection
- **Logcat** (0.4) - Structured logging library for Android

### Distribution-specific libraries
- **Play only: Google Mobile Ads SDK** (25.4.0) - AdMob banner ads; isolated to `playImplementation`
- **Play only: User Messaging Platform** (4.0.0) - Official consent and privacy-options flow; isolated to `playImplementation`
- **FOSS only: none** - No Google Play Services, AdMob, UMP, or automatic network runtime dependency
- **Kotlinx Serialization JSON** (1.9.0) - Parses the offline Licensee catalog in the common license screen

## Testing Libraries

### Unit Testing
- **JUnit** (4.13.2) - Standard Java testing framework for unit tests
- **Kotlin Test** (2.3.21) - Kotlin-specific testing utilities

### Android Testing
- **AndroidX Test JUnit** (1.3.0) - JUnit integration for Android instrumented tests
- **AndroidX Test Runner** (1.7.0) - Test runner for Android instrumented tests
- **AndroidX Test UI Automator** (2.4.0) - UI testing framework for cross-app interactions

### Mocking
- **MockK** (1.14.11) - Mocking library for Kotlin unit tests
- **MockK Android** (1.14.11) - Android-specific MockK extensions
- **MockK Agent** (1.14.11) - JVM agent for MockK advanced features

## Purpose in Aplin

Aplin is an Android application manager that helps users view and manage installed applications. The dependencies serve the following purposes:

- **Compose libraries** build the modern, declarative UI
- **Navigation** handles moving between different app screens
- **Koin** manages dependency injection for clean architecture
- **DataStore/Preferences** store user settings and app state
- **Play distribution libraries** handle AdMob advertising and Google's UMP consent flow
- **Licensee** generates the offline dependency-license catalog at build time for both variants
- **Testing libraries** ensure code quality and reliability
- **Logcat** provides structured logging for debugging

This dependency structure supports a modern Android app with Material Design UI, proper testing coverage, and compliance with Play Store requirements.

## Historical Migration Notes

### Gradle 8.14.5 Update
- Updated the Gradle Wrapper from 8.14.2 to 8.14.5 as a patch update for bug fixes
- No breaking changes expected for this patch update

### Gradle 8.11 Update
- Updated from Gradle 8.9 to 8.11 for latest build performance improvements and bug fixes
- No breaking changes expected for this minor version update

### Android Gradle Plugin 8.7.0 Update
- Updated from AGP 8.6.1 to 8.7.0 for latest Android build tooling
- This update includes the latest Android build optimizations and bug fixes
- No breaking changes expected for this minor version update

### Koin 4.0.0 Update
The update from Koin 3.5.6 to 4.0.0 is a major version change that may require code modifications:
- Review dependency injection setup for any breaking changes
- Check if any Koin API usage needs updating
- Verify all modules and injections work correctly after the update

### Kotlin 2.3.21 Update
- Ensure compatibility with Kotlin 2.3.x features
- Verify all Kotlin extensions and language features work correctly

### Compose 1.7.5 Update
- Latest Compose UI updates may include new features and optimizations
- Verify all Compose components render correctly

## Distribution matrix (2026-08-29)

- `play` keeps the existing Play application ID `com.nagopy.android.aplin`; `foss` adds the `.foss` application ID suffix.
- Both variants use compile/target SDK 36, min SDK 26, versionCode 46, and versionName 5.6.0.
- Both variants keep `QUERY_ALL_PACKAGES` because package classification is Aplin's core on-device feature.
- Only Play has AdMob/UMP dependencies, AdMob manifest metadata, and INTERNET/ACCESS_NETWORK_STATE. The Google Mobile Ads SDK may contribute AD_ID to the Play merged manifest; FOSS removes it defensively and contains no matching runtime classes.
- FOSS has no ads, UMP, automatic communication, or INTERNET/ACCESS_NETWORK_STATE/AD_ID permission. Web lookup is only a user-initiated `ACTION_WEB_SEARCH` intent handled by another app.
- Both variants display the dependency catalog offline from Licensee's generated `assets/app/cash/licensee/artifacts.json`. Google OSS Licenses plugin/library has been removed.

## Common Dependency Update (2026-08-29)

- AndroidX stable versions were checked against the Android Developers stable release table and Google Maven metadata. Compose UI, Material, and Tooling are kept on the same `1.11.4` line, which is compatible with the unchanged compileSdk 36 and AGP 8.13.2. Material Icons Core remains on its separately published latest stable `1.7.8` artifact line.
- `espresso-core`, `androidx.test:rules`, and Compose `ui-test-junit4` were removed because `rg` found no source or test usage. The remaining AndroidX Test dependencies are used by the instrumented tests.
- AppCompat was updated from `1.7.0` to `1.7.1` and remains a direct dependency because `themes.xml` directly uses `Theme.AppCompat.Light.NoActionBar`; the four preview calls continue to use `AppCompatResources.getDrawable`.
- Lifecycle `2.11.0` was not adopted because atomic alignment of `lifecycle-runtime-compose` and `lifecycle-viewmodel-compose` requires compileSdk 37 and AGP 9.1; `2.10.0` is the compatible stable line for this unchanged build setup.
- No direct Material Components dependency is declared or resolved in the release runtime graph.
- Google Mobile Ads SDK 25.4.0 and UMP 4.0.0 are Play-only. The official UMP flow requests consent information at every launch, loads and shows required forms, checks `canRequestAds()`, exposes privacy options only when required, and does not read cached TCF values in application code.
- Cash App Licensee 1.14.1 is build-time only. Its generated report is bundled at the documented `assets/app/cash/licensee/artifacts.json` path and is parsed by the app without a Licensee runtime dependency.
- The in-app catalog displays coordinates and every license name, identifier, and URL present in that generated metadata. It is not a substitute for full NOTICE/copyright texts; F-Droid metadata and asset/notice licensing remain separate release work.
- Managed-emulator CI, reproducible-build proof, full third-party NOTICE/copyright text inclusion, and release signing remain outside this distribution change.

## Post-Update Verification Checklist

Use this checklist to verify dependency and build changes:

1. **Build Verification**
   - `./gradlew clean build` - Ensure project builds successfully
   - Check for any compilation errors or warnings

2. **Testing**
   - `./gradlew test` - Run unit tests
   - `./gradlew connectedAndroidTest` - Run instrumented tests
   - Verify all tests pass

3. **Koin Migration Verification**
   - Test dependency injection functionality
   - Verify all activities and fragments receive proper dependencies
   - Check for any runtime injection errors

4. **UI Testing**
   - Launch the app and navigate through all screens
   - Verify Compose UI renders correctly
   - Test app functionality end-to-end

5. **Performance**
   - Monitor app startup time
   - Check for any new ANRs or crashes
   - Verify memory usage is within expected ranges
