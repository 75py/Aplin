import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApp)
    alias(libs.plugins.kotlinAndroid)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.ktlint)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.nagopy.android.aplin"

    defaultConfig {
        applicationId = "com.nagopy.android.aplin"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 43
        versionName = "5.4.3"

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
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaVersion.get()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerExtVersion.get()
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
