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

The Play release is available [on Google Play](https://play.google.com/store/apps/details?id=com.nagopy.android.aplin). F-Droid metadata is maintained in the supported root layout under `metadata/<locale>`. Publishing to F-Droid still requires a future immutable release tag/commit and a separate submission to the external `fdroiddata` repository; this repository does not add a recipe for a release that does not exist.

## Build

```sh
./gradlew testPlayDebugUnitTest testFossDebugUnitTest
./gradlew lintPlayRelease lintFossRelease
./gradlew bundlePlayRelease
./gradlew assembleFossRelease
sh scripts/verify-foss-apk.sh
```

Play release requires a local, untracked `ads.properties` containing correctly formatted real `appId` and `unitId` values. For non-publishable local or CI checks only, pass `-PallowTestAds=true`; Play debug uses Google's official test IDs. FOSS artifacts do not use or package values from this file. `bundlePlayRelease` is unsigned unless a signing configuration is supplied separately.

## Privacy and variants

Both variants use `QUERY_ALL_PACKAGES` only to enumerate and classify packages already installed on the device. Neither variant automatically collects the installed-app list or uploads it in the background. If you explicitly choose Share, package names in the shared list are sent to the external share target you select. A user-initiated web search sends one app label and package name in an `ACTION_WEB_SEARCH` intent to another installed handler; Aplin does not perform the web request itself.

The Play build adds AdMob and Google's UMP consent flow, plus its Play-only network permissions and advertising runtime. The FOSS build has none of those dependencies or advertising metadata. Its dependency license catalog is generated at build time and displayed offline from `assets/app/cash/licensee/artifacts.json`; Licensee is not an application runtime dependency.

The common launcher artwork and F-Droid metadata icon are documented in [ASSET-LICENSES.md](ASSET-LICENSES.md). The F-Droid listing metadata is kept separate from Play Fastlane metadata under `metadata/<locale>`.

## FOSS APK verification

CI builds `assembleFossRelease` and then runs `scripts/verify-foss-apk.sh`. The small static smoke check uses a validated `aapt2`, checks the FOSS package, version, target SDK, exact two-permission allowlist, ZIP integrity and member names, a non-empty Licensee catalog with no forbidden exact dependency group IDs, DEX strings, and `aapt2` manifest/resource dumps for known Play advertising, UMP, and Google OSS license runtime identifiers. It does not prove signatures, unknown obfuscated code, reproducible builds, device behavior, or F-Droid acceptance.

The future release tag/commit, release signing, external `fdroiddata` submission/review, managed-emulator coverage, and complete third-party NOTICE/copyright text remain separate release work.

## License

Aplin's source code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).

## Contribute

Contributions to Aplin are always welcome! Whether it's feature enhancements, bug fixes, or documentation improvements, we'd love to have you onboard.

## Note

I'm not fluent in English, so please be kind in your interactions and communication regarding Aplin.

Thank you for your understanding and support!
