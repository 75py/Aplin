buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.licenseePlugin)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApp) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinPluginCompose) apply false
    alias(libs.plugins.ktlint)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
