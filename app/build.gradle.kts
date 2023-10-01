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
    implementation(libs.androidxKtx)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeToolingPreview)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxTestExtJunit)
    androidTestImplementation(libs.androidxTestEspressoCore)
    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.androidxTestRules)
    androidTestImplementation(libs.androidxTestUiautomator)
    androidTestImplementation(libs.kotlinTest)
    androidTestImplementation(libs.androidxComposeUiTestJunit4)
    debugImplementation(libs.androidxComposeUiTooling)

    implementation(libs.koinAndroid)
    implementation(libs.kotlinReflect)
    implementation(libs.accompanistDrawablepainter)
    implementation(libs.logcat)

    implementation(libs.navigationCompose)

    implementation(libs.playServicesOssLicenses)
    implementation(libs.appcompat)

    implementation(libs.playServicesAds)

    implementation(libs.ump)
    implementation(libs.preferenceKtx)

    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockkAndroid)
    androidTestImplementation(libs.mockkAgent)

    implementation(libs.composePrefs)
    implementation(libs.datastorePreferences)
}

fun readProperties(propertiesFile: File) = Properties().apply {
    propertiesFile.inputStream().use { fis ->
        load(fis)
    }
}
