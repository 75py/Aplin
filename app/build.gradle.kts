import com.android.build.gradle.api.ApplicationVariant
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApp)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinPluginCompose)
    id("app.cash.licensee")
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}

val allowTestAds = project.providers.gradleProperty("allowTestAds").orNull == "true"

android {
    compileSdk =
        libs.versions.compileSdk
            .get()
            .toInt()
    namespace = "com.nagopy.android.aplin"

    defaultConfig {
        applicationId = "com.nagopy.android.aplin"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode = 46
        versionName = "5.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("play") {
            dimension = "distribution"
            applicationId = "com.nagopy.android.aplin"
            if (allowTestAds) {
                applicationIdSuffix = ".ci"
            }
        }
        create("foss") {
            dimension = "distribution"
            applicationIdSuffix = ".foss"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // signingConfig = signingConfigs.getByName("debug")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

android.applicationVariants.all {
    configurePlayAds(this)
}

val validatePlayReleaseAds =
    tasks.register("validatePlayReleaseAds") {
        doLast {
            val adsPropertiesFile = project.rootProject.file("ads.properties")
            check(adsPropertiesFile.isFile) {
                "Play release requires ${adsPropertiesFile.path} with appId and unitId; refusing to use test IDs."
            }
            val adsProperties = readProperties(adsPropertiesFile)
            val appId = adsProperties.requireValue("appId")
            val unitId = adsProperties.requireValue("unitId")
            val allowTestAds = project.providers.gradleProperty("allowTestAds").orNull == "true"

            validateAdMobId(
                name = "appId",
                value = appId,
                pattern = admobAppIdPattern,
            )
            validateAdMobId(
                name = "unitId",
                value = unitId,
                pattern = admobUnitIdPattern,
            )
            if (!allowTestAds) {
                check(appId !in googleTestAppIds && unitId !in googleTestUnitIds) {
                    "Google test ad IDs are not allowed for Play release; use real IDs or explicitly pass -PallowTestAds=true."
                }
            }
        }
    }

tasks.configureEach {
    val isPlayReleaseLifecycleTask =
        name != validatePlayReleaseAds.name &&
            (
                name == "prePlayReleaseBuild" ||
                    (
                        name.contains("PlayRelease") &&
                            setOf("assemble", "bundle", "package", "lint").any(name::startsWith)
                    )
            )
    if (isPlayReleaseLifecycleTask) {
        dependsOn(validatePlayReleaseAds)
    }
}

licensee {
    allow("Apache-2.0")
    allow("BSD-2-Clause")
    allow("BSD-3-Clause")
    allow("MIT")
    allow("ISC")
    allowUrl("https://developer.android.com/studio/terms.html")
    bundleAndroidAsset = true
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
    }
}

dependencies {
    implementation(libs.androidxKtx)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.composeMaterialIconsCore)
    implementation(libs.composeToolingPreview)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.activityCompose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxTestExtJunit)
    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.androidxTestUiautomator)
    androidTestImplementation(libs.kotlinTest)
    debugImplementation(libs.androidxComposeUiTooling)

    implementation(libs.koinAndroid)
    implementation(libs.kotlinReflect)
    implementation(libs.logcat)

    implementation(libs.navigationCompose)

    implementation(libs.appcompat)

    add("playImplementation", libs.playServicesAds)
    add("playImplementation", libs.ump)

    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockkAndroid)
    androidTestImplementation(libs.mockkAgent)

    implementation(libs.datastorePreferences)
    implementation(libs.kotlinxSerializationJson)
}

fun configurePlayAds(variant: ApplicationVariant) {
    if (variant.flavorName != "play") {
        return
    }

    val (appId, unitId) =
        if (variant.buildType.name == "debug") {
            "ca-app-pub-3940256099942544~3347511713" to "ca-app-pub-3940256099942544/6300978111"
        } else {
            val adsPropertiesFile = project.rootProject.file("ads.properties")
            if (adsPropertiesFile.isFile) {
                val adsProperties = readProperties(adsPropertiesFile)
                adsProperties.valueOrMissing("appId") to adsProperties.valueOrMissing("unitId")
            } else {
                "__MISSING_ADS_PROPERTIES__" to "__MISSING_ADS_PROPERTIES__"
            }
        }

    variant.buildConfigField("String", "AD_UNIT_ID", "\"$unitId\"")
    variant.resValue("string", "ad_app_id", appId)
}

fun Properties.requireValue(name: String): String =
    getProperty(name)?.trim()?.takeIf { it.isNotEmpty() }
        ?: error("ads.properties is missing non-empty $name for Play release")

fun Properties.valueOrMissing(name: String): String =
    getProperty(name)
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "__MISSING_ADS_PROPERTIES__"

fun validateAdMobId(
    name: String,
    value: String,
    pattern: Regex,
) {
    check(value != "__MISSING_ADS_PROPERTIES__") {
        "ads.properties contains a placeholder for $name"
    }
    check(!value.contains("placeholder", ignoreCase = true)) {
        "ads.properties contains a placeholder for $name"
    }
    check(pattern.matches(value)) {
        "ads.properties $name must match an AdMob ID format"
    }
}

val admobAppIdPattern = Regex("ca-app-pub-[0-9]{16}~[0-9]{10}")
val admobUnitIdPattern = Regex("ca-app-pub-[0-9]{16}/[0-9]{10}")
val googleTestAppIds = setOf("ca-app-pub-3940256099942544~3347511713")
val googleTestUnitIds = setOf("ca-app-pub-3940256099942544/6300978111")

fun readProperties(propertiesFile: File) =
    Properties().apply {
        propertiesFile.inputStream().use { fis ->
            load(fis)
        }
    }
