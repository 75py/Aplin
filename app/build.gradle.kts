import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.gms.oss-licenses-plugin")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdkVersion(34)
    namespace = "com.nagopy.android.aplin"

    defaultConfig {
        applicationId = "com.nagopy.android.aplin"
        minSdkVersion(26)
        targetSdkVersion(34)
        versionCode = 40
        versionName = "5.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            val adsProperties = readProperties(File("ads.properties"))
            buildConfigField("String", "AD_UNIT_ID", "\"${adsProperties["unitId"]}\"")
            resValue("string", "ad_app_id", "\"${adsProperties["appId"]}\"")
            // signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            resValue("string", "ad_app_id", "ca-app-pub-3940256099942544~3347511713")
            buildConfigField("String", "AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.5.2")
    implementation("androidx.compose.material:material:1.5.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.2")

    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.32.0")
    implementation("com.squareup.logcat:logcat:0.1")

    implementation("androidx.navigation:navigation-compose:2.7.3")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.google.android.gms:play-services-ads:22.4.0")

    implementation("com.google.android.ump:user-messaging-platform:2.1.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    testImplementation("io.mockk:mockk:1.13.8")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation("io.mockk:mockk-agent:1.13.8")

    implementation("com.github.JamalMulla:ComposePrefs:1.0.2")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}
