buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.ossLicenses)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    // alias(libs.plugins.androidApp) apply false
    // alias(libs.plugins.androidLib) apply false
    // alias(libs.plugins.kotlinAndroid) apply false
    // alias(libs.plugins.kotlinPluginCompose) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
