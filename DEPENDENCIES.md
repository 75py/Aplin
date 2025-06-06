# Dependencies

This document provides an overview of all Gradle dependencies used in the Aplin project and their purpose within the application.

## SDK Configuration

- **Compile SDK**: 35 - Target SDK version for compilation
- **Min SDK**: 26 - Minimum Android API level supported (Android 8.0)
- **Target SDK**: 35 - Target Android API level (Android 15)
- **Java Version**: 17 - Java language version used for compilation

## Build Plugins

### Core Android Plugins
- **Android Gradle Plugin** (8.7.0) - Essential plugin for Android app development, handles Android-specific build tasks
- **Kotlin Android Plugin** (2.0.20) - Enables Kotlin language support in Android projects
- **Kotlin Compose Plugin** (2.0.20) - Provides Kotlin compiler support for Jetpack Compose

### Code Quality & Analysis
- **ktlint** (11.6.1) - Kotlin linter and formatter for consistent code style
- **Kover** (0.8.3) - Code coverage tool for Kotlin projects

### License Management
- **OSS Licenses Plugin** (0.10.6) - Generates open source license reports for compliance

## Core Libraries

### Android Core
- **AndroidX Core KTX** (1.13.1) - Kotlin extensions for Android core APIs, providing more concise and idiomatic code
- **AppCompat** (1.7.0) - Backward compatibility library for modern Android features on older devices

### Jetpack Compose UI Framework
- **Compose UI** (1.7.3) - Core Jetpack Compose library for building native Android UI
- **Compose Material** (1.7.3) - Material Design components for Compose
- **Compose Material Icons Extended** (1.7.3) - Extended set of Material Design icons for Compose
- **Compose UI Tooling Preview** (1.7.3) - Preview support for Compose in Android Studio
- **Compose UI Tooling** (1.7.3) - Debug tooling for Compose (debug builds only)

### Lifecycle & Activity
- **Lifecycle Runtime KTX** (2.8.6) - Lifecycle-aware components with Kotlin extensions
- **Activity Compose** (1.9.2) - Integration between Activities and Jetpack Compose

### Navigation
- **Navigation Compose** (2.8.2) - Navigation component for Jetpack Compose, handles in-app navigation

### Dependency Injection
- **Koin Android** (3.5.6) - Lightweight dependency injection framework for Android

### Data & Preferences
- **DataStore Preferences** (1.1.1) - Modern replacement for SharedPreferences with type safety and coroutine support
- **Preference KTX** (1.2.1) - Kotlin extensions for Android preferences
- **ComposePrefs** (1.0.6) - Preference components built for Jetpack Compose

### Utilities
- **Kotlin Reflect** (2.0.20) - Kotlin reflection library for runtime introspection
- **Accompanist Drawable Painter** (0.36.0) - Utility for using Android drawables in Compose
- **Logcat** (0.1) - Structured logging library for Android

### Google Play Services
- **Play Services Ads** (23.4.0) - Google Mobile Ads SDK for displaying advertisements
- **Play Services OSS Licenses** (17.1.0) - Library for displaying open source licenses
- **User Messaging Platform (UMP)** (3.0.0) - Google's solution for GDPR and privacy compliance

## Testing Libraries

### Unit Testing
- **JUnit** (4.13.2) - Standard Java testing framework for unit tests
- **Kotlin Test** (2.0.20) - Kotlin-specific testing utilities

### Android Testing
- **AndroidX Test JUnit** (1.2.1) - JUnit integration for Android instrumented tests
- **AndroidX Test Espresso Core** (3.6.1) - UI testing framework for Android
- **AndroidX Test Runner** (1.6.2) - Test runner for Android instrumented tests
- **AndroidX Test Rules** (1.6.1) - Test rules for Android testing
- **AndroidX Test UI Automator** (2.3.0) - UI testing framework for cross-app interactions

### Compose Testing
- **Compose UI Test JUnit4** (1.7.3) - Testing utilities for Jetpack Compose UIs

### Mocking
- **MockK** (1.13.12) - Mocking library for Kotlin unit tests
- **MockK Android** (1.13.12) - Android-specific MockK extensions
- **MockK Agent** (1.13.12) - JVM agent for MockK advanced features

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