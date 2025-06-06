# Dependencies

This document provides an overview of all Gradle dependencies used in the Aplin project and their purpose within the application.

## SDK Configuration

- **Gradle Wrapper**: 8.12 - Build automation tool version
- **Compile SDK**: 35 - Target SDK version for compilation
- **Min SDK**: 26 - Minimum Android API level supported (Android 8.0)
- **Target SDK**: 35 - Target Android API level (Android 15)
- **Java Version**: 17 - Java language version used for compilation

## Build Plugins

### Core Android Plugins
- **Android Gradle Plugin** (8.10.0) - Essential plugin for Android app development, handles Android-specific build tasks
- **Kotlin Android Plugin** (2.0.21) - Enables Kotlin language support in Android projects
- **Kotlin Compose Plugin** (2.0.21) - Provides Kotlin compiler support for Jetpack Compose

### Code Quality & Analysis
- **ktlint** (12.1.1) - Kotlin linter and formatter for consistent code style
- **Kover** (0.8.3) - Code coverage tool for Kotlin projects

### License Management
- **OSS Licenses Plugin** (0.10.6) - Generates open source license reports for compliance

## Core Libraries

### Android Core
- **AndroidX Core KTX** (1.15.0) - Kotlin extensions for Android core APIs, providing more concise and idiomatic code
- **AppCompat** (1.7.0) - Backward compatibility library for modern Android features on older devices

### Jetpack Compose UI Framework
- **Compose UI** (1.7.5) - Core Jetpack Compose library for building native Android UI
- **Compose Material** (1.7.5) - Material Design components for Compose
- **Compose Material Icons Extended** (1.7.5) - Extended set of Material Design icons for Compose
- **Compose UI Tooling Preview** (1.7.5) - Preview support for Compose in Android Studio
- **Compose UI Tooling** (1.7.5) - Debug tooling for Compose (debug builds only)

### Lifecycle & Activity
- **Lifecycle Runtime KTX** (2.8.7) - Lifecycle-aware components with Kotlin extensions
- **Activity Compose** (1.9.3) - Integration between Activities and Jetpack Compose

### Navigation
- **Navigation Compose** (2.8.4) - Navigation component for Jetpack Compose, handles in-app navigation

### Dependency Injection
- **Koin Android** (4.0.0) - Lightweight dependency injection framework for Android

### Data & Preferences
- **DataStore Preferences** (1.1.1) - Modern replacement for SharedPreferences with type safety and coroutine support
- **Preference KTX** (1.2.1) - Kotlin extensions for Android preferences
- **ComposePrefs** (1.0.6) - Preference components built for Jetpack Compose

### Utilities
- **Kotlin Reflect** (2.0.21) - Kotlin reflection library for runtime introspection
- **Accompanist Drawable Painter** (0.36.0) - Utility for using Android drawables in Compose
- **Logcat** (0.1) - Structured logging library for Android

### Google Play Services
- **Play Services Ads** (23.5.0) - Google Mobile Ads SDK for displaying advertisements
- **Play Services OSS Licenses** (17.1.0) - Library for displaying open source licenses
- **User Messaging Platform (UMP)** (3.1.0) - Google's solution for GDPR and privacy compliance

## Testing Libraries

### Unit Testing
- **JUnit** (4.13.2) - Standard Java testing framework for unit tests
- **Kotlin Test** (2.0.21) - Kotlin-specific testing utilities

### Android Testing
- **AndroidX Test JUnit** (1.2.1) - JUnit integration for Android instrumented tests
- **AndroidX Test Espresso Core** (3.6.1) - UI testing framework for Android
- **AndroidX Test Runner** (1.6.2) - Test runner for Android instrumented tests
- **AndroidX Test Rules** (1.6.1) - Test rules for Android testing
- **AndroidX Test UI Automator** (2.3.0) - UI testing framework for cross-app interactions

### Compose Testing
- **Compose UI Test JUnit4** (1.7.5) - Testing utilities for Jetpack Compose UIs

### Mocking
- **MockK** (1.13.13) - Mocking library for Kotlin unit tests
- **MockK Android** (1.13.13) - Android-specific MockK extensions
- **MockK Agent** (1.13.13) - JVM agent for MockK advanced features

## Purpose in Aplin

Aplin is an Android application manager that helps users view and manage installed applications. The dependencies serve the following purposes:

- **Compose libraries** build the modern, declarative UI
- **Navigation** handles moving between different app screens
- **Koin** manages dependency injection for clean architecture
- **DataStore/Preferences** store user settings and app state
- **Play Services** handle advertisements and license compliance
- **Testing libraries** ensure code quality and reliability
- **Logcat** provides structured logging for debugging

This dependency structure supports a modern Android app with Material Design UI, proper testing coverage, and compliance with Play Store requirements.

## Migration Notes

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

### Kotlin 2.0.21 Update
- Ensure compatibility with Kotlin 2.0.x features
- Verify all Kotlin extensions and language features work correctly

### Compose 1.7.5 Update
- Latest Compose UI updates may include new features and optimizations
- Verify all Compose components render correctly

## Post-Update Verification Checklist

After resolving network connectivity issues, perform these verification steps:

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