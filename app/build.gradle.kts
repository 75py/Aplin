import com.android.build.gradle.api.ApplicationVariant
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.security.MessageDigest
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

// Keep the reviewed release notices in sync with each distribution's actual graph.
// Debug builds display the same release notices, excluding development-only tooling.
listOf("play", "foss").forEach { distribution ->
    val capitalized = distribution.replaceFirstChar(Char::uppercaseChar)
    val report = layout.buildDirectory.file("reports/licensee/android${capitalized}Release/artifacts.json")
    val notices = file("src/$distribution/assets/licenses/third-party-notices.txt")
    val manifest = rootProject.file("licenses/$distribution.json")

    tasks.register("export${capitalized}NoticeArtifacts") {
        dependsOn("licenseeAndroid${capitalized}Release")
        val output = layout.buildDirectory.file("reports/third-party-notices/$distribution-artifacts.json")
        outputs.file(output)
        // The output contains local cache paths, so always refresh it on request.
        outputs.upToDateWhen { false }
        doLast {
            val artifacts =
                configurations
                    .getByName("${distribution}ReleaseRuntimeClasspath")
                    .resolvedConfiguration.resolvedArtifacts
                    .sortedBy { "${it.moduleVersion.id}:${it.file.name}" }
                    .map { mapOf("coordinate" to it.moduleVersion.id.toString(), "file" to it.file.absolutePath) }
            output.get().asFile.apply {
                parentFile.mkdirs()
                writeText(JsonOutput.prettyPrint(JsonOutput.toJson(artifacts)) + "\n")
            }
        }
    }

    val validateNotices =
        tasks.register("validate${capitalized}ThirdPartyNotices") {
            dependsOn("licenseeAndroid${capitalized}Release")
            inputs.file(report)
            inputs.file(manifest)
            inputs.file(notices)
            inputs.files(provider { configurations.getByName("${distribution}ReleaseRuntimeClasspath") })
            doLast {
                val recorded = JsonSlurper().parse(manifest) as Map<*, *>
                val artifacts = JsonSlurper().parse(report.get().asFile) as List<*>
                val coordinates =
                    artifacts
                        .map { entry ->
                            val artifact = entry as Map<*, *>
                            "${artifact["groupId"]}:${artifact["artifactId"]}:${artifact["version"]}"
                        }.toSet()
                val expected = (recorded["dependencies"] as List<*>).toSet()
                check(coordinates == expected) {
                    "Third-party notices are stale for $distribution. Review dependency LICENSE/NOTICE changes, " +
                        "run export${capitalized}NoticeArtifacts and scripts/update-third-party-notices.py. " +
                        "Added: ${coordinates - expected}; removed: ${expected - coordinates}"
                }
                val actualHash =
                    MessageDigest
                        .getInstance("SHA-256")
                        .digest(notices.readBytes())
                        .joinToString("") { "%02x".format(it) }
                check(actualHash == recorded["noticesSha256"]) {
                    "Bundled third-party notices differ from the reviewed $distribution manifest. Regenerate and review them."
                }
                val actualArtifacts =
                    configurations
                        .getByName("${distribution}ReleaseRuntimeClasspath")
                        .resolvedConfiguration.resolvedArtifacts
                        .map { artifact ->
                            val hash =
                                MessageDigest
                                    .getInstance("SHA-256")
                                    .digest(artifact.file.readBytes())
                                    .joinToString("") { "%02x".format(it) }
                            "${artifact.moduleVersion.id}:${artifact.file.name}:$hash"
                        }.toSet()
                val recordedArtifacts =
                    (recorded["artifacts"] as List<*>)
                        .map { entry ->
                            val artifact = entry as Map<*, *>
                            "${artifact["coordinate"]}:${artifact["file"]}:${artifact["sha256"]}"
                        }.toSet()
                check(actualArtifacts == recordedArtifacts) {
                    "Resolved $distribution AAR/JAR contents differ from the reviewed notices. Review and regenerate them."
                }
            }
        }
    tasks.configureEach {
        if (name == "pre${capitalized}ReleaseBuild" || name == "pre${capitalized}DebugBuild") {
            dependsOn(validateNotices)
        }
    }
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
