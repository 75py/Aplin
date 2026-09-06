# Aplin

## Introduction
Aplin is an Android application manager that provides a comprehensive list of installed applications on your device. With Aplin, you can easily view:

- Apps that can be uninstalled
- Apps that can be disabled
- Already disabled apps

Simplify your device management with Aplin!

## Download
Aplin has two distribution variants:

| Variant | Application ID | Intended channel | Ads and privacy messaging |
| --- | --- | --- | --- |
| `play` | `com.nagopy.android.aplin` | Google Play, Japan | AdMob and Google's UMP |
| `foss` | `com.nagopy.android.aplin.foss` | F-Droid, worldwide | No ads, UMP, Google Play Services runtime, or network permissions |

The Play release is available [on Google Play](https://play.google.com/store/apps/details?id=com.nagopy.android.aplin). F-Droid metadata and asset licensing submissions are separate release work.

## Build

```sh
./gradlew testPlayDebugUnitTest testFossDebugUnitTest
./gradlew lintPlayRelease lintFossRelease
./gradlew bundlePlayRelease
./gradlew assembleFossRelease
```

Play release requires a local, untracked `ads.properties` containing a correctly formatted real `appId` and `unitId`. Missing, placeholder, malformed, or known Google test IDs fail release validation. For non-publishable local/CI checks only, pass `-PallowTestAds=true`; this adds the `.ci` suffix, so `bundlePlayRelease` uses `com.nagopy.android.aplin.ci` and cannot be confused with the normal `com.nagopy.android.aplin` release. Play debug uses Google's official test IDs. FOSS builds do not read this file. `bundlePlayRelease` is unsigned unless a signing configuration is supplied separately.

The open source license screen reads Cash App Licensee's bundled dependency catalog offline and provides a separate view of complete bundled third-party license and notice texts. The texts include upstream archive notices, reviewed supplemental copyright/NOTICE files, Google SDK third-party notices in Play, and Android robot artwork attribution. Licensee itself is not an application runtime dependency. See [license provenance and update instructions](licenses/README.md).

FOSS uses `QUERY_ALL_PACKAGES` only to classify packages already installed on the device. It does not use that permission for network access or data collection.

Managed-emulator CI, reproducible-build proof, F-Droid submission, and release signing remain separate release work.

## License
Aplin's source code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).

## Contribute
Contributions to Aplin are always welcome! Whether it's feature enhancements, bug fixes, or documentation improvements, we'd love to have you onboard.

## Note
I'm not fluent in English, so please be kind in your interactions and communication regarding Aplin.

Thank you for your understanding and support!
