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

The open source license screen is generated from Cash App Licensee's build-time report and reads the bundled `assets/app/cash/licensee/artifacts.json` file offline. It displays dependency coordinates and the license metadata present in that generated list, not complete NOTICE/copyright texts. Licensee itself is not an application runtime dependency. F-Droid metadata and asset/notice licensing remain separate release work.

FOSS uses `QUERY_ALL_PACKAGES` only to classify packages already installed on the device. It does not use that permission for network access or data collection.

Managed-emulator CI, reproducible-build proof, full third-party NOTICE/copyright text inclusion, and release signing are outside this distribution split and remain separate release work.

## License
Aplin's source code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).

## Contribute
Contributions to Aplin are always welcome! Whether it's feature enhancements, bug fixes, or documentation improvements, we'd love to have you onboard.

## Note
I'm not fluent in English, so please be kind in your interactions and communication regarding Aplin.

Thank you for your understanding and support!
