package com.nagopy.android.aplin.domain.usecase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.data.repository.DevicePolicyRepository
import com.nagopy.android.aplin.data.repository.PackageRepository
import java.lang.reflect.Field

class CategorizePackageUseCase(
    private val packageRepository: PackageRepository,
    private val devicePolicyRepository: DevicePolicyRepository
) {

    private val systemPackageSignatures: List<String> by lazy {
        getSignatureStrings(packageRepository.systemPackage)
    }

    private fun isSystemPackage(packageInfo: PackageInfo?): Boolean {
        return packageInfo != null &&
            (
                isThisASystemPackage(packageInfo) ||
                    packageInfo.packageName == packageRepository.permissionControllerPackageName ||
                    packageInfo.packageName == packageRepository.servicesSystemSharedLibraryPackageName ||
                    packageInfo.packageName == packageRepository.sharedSystemSharedLibraryPackageName ||
                    packageInfo.packageName == packageRepository.printSpoolerPackageName ||
                    packageInfo.packageName == packageRepository.deviceProvisioningPackage
                )
    }

    private fun getSignatureStrings(packageInfo: PackageInfo?): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.signingInfo?.let { signingInfo ->
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners.map { sig -> sig.toCharsString() }
                } else {
                    signingInfo.signingCertificateHistory.map { sig -> sig.toCharsString() }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            packageInfo?.signatures?.map { sig -> sig.toCharsString() }
        } ?: emptyList()
    }

    private fun isThisASystemPackage(packageInfo: PackageInfo?): Boolean {
        val signatures = getSignatureStrings(packageInfo)
        return systemPackageSignatures == signatures
    }

    fun isBundled(packageInfo: PackageInfo): Boolean {
        val flags = packageInfo.applicationInfo?.flags
        if (flags == null) {
            return false
        }
        return flags and ApplicationInfo.FLAG_SYSTEM > 0
    }

    fun isDisableable(
        packageInfo: PackageInfo,
        homePackages: Set<String>,
        currentDefaultHomePackageName: String?
    ): Boolean {
        val isBundled = isBundled(packageInfo)
        var enabled = true
        if (isBundled) {
            enabled = handleDisableable(packageInfo, homePackages)
        } else {
            // not system app
            return false
        }

        if (isBundled && devicePolicyRepository.packageHasActiveAdmins(packageInfo.packageName)) {
            enabled = false
        }

        // 不完全な再現
        if (devicePolicyRepository.isProfileOrDeviceOwner(packageInfo.packageName)) {
            enabled = false
        }

        // not implemented: R.string.config_deviceProvisioningPackage

        if (enabled && homePackages.contains(packageInfo.packageName)) {
            if (isBundled) {
                enabled = false
            } else {
                if (homePackages.size == 1) {
                    enabled = false
                } else {
                    if (currentDefaultHomePackageName == packageInfo.packageName) {
                        enabled = false
                    }
                }
            }
        }

        // not implemented: hasBaseUserRestriction

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && packageInfo.applicationInfo?.isResourceOverlay == true) {
            if (isBundled) {
                enabled = false
            } else {
                // not implemented
            }
        }

        return enabled
    }

    private fun handleDisableable(packageInfo: PackageInfo, homePackages: Set<String>): Boolean {
        var disableable = false
        if (homePackages.contains(packageInfo.packageName) || isSystemPackage(packageInfo)
        ) {
            // Disable button for core system applications.
        } else if (packageInfo.applicationInfo?.enabled == true && !isDisabledUntilUsed(packageInfo)) {
            disableable = !keepEnabledPackages.contains(packageInfo.applicationInfo?.packageName)
        } else {
            disableable = true
        }
        return disableable
    }

    private val enabledSettingField: Field? by lazy {
        try {
            ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
                isAccessible = true
            }
        } catch (t: Throwable) {
            null
        }
    }

    private fun isDisabledUntilUsed(packageInfo: PackageInfo): Boolean {
        val enabledSetting = enabledSettingField?.get(packageInfo.applicationInfo)
        return enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
    }

    // not implemented: DefaultDialerManager.getDefaultDialerApplication
    // not implemented: SmsApplication.getDefaultSmsApplication
    // not implemented: locationManager.getExtraLocationControllerPackage
    private val keepEnabledPackages = setOf(
        // R.string.config_settingsintelligence_package_name
        "com.android.settings.intelligence",
        // R.string.config_package_installer_package_name
        "com.android.packageinstaller",

        // Pixel 3a, Android 12
        "com.google.android.ims",
        "com.google.android.cellbroadcastservice",
        "com.google.android.apps.wellbeing",
        "com.google.android.inputmethod.latin",
        "com.google.android.gms.location.history",
        "com.google.android.hotspot2.osulogin",
        "com.google.android.euicc",
        "com.google.android.networkstack.tethering",
        "com.android.cts.ctsshim",
        "com.android.cts.priv.ctsshim",
        "com.google.android.wifi.resources",
        "com.google.android.connectivity.resources",
        "com.google.android.packageinstaller",
        "com.google.android.apps.messaging",
        "com.google.android.providers.media.module",
        "com.google.android.cellbroadcastreceiver",
        "com.google.android.settings.intelligence",
        "com.google.android.dialer",

        // Emulator, Android 11
        "com.android.dialer",

        // Emulator, Android 13 Preview
        "com.android.uwb.resources",
        "com.android.supplemental.process",
        "com.google.android.auxiliary.service",
        "com.google.android.nearby.halfsheet",
        "com.google.android.ondevicepersonalization.services",
        "com.google.android.wifi.dialog",

        // Emulator, Android Tiramisu Privacy Sandbox
        "com.google.android.adservices.api",
        "com.google.android.bluetooth.services",
        "com.google.android.safetycenter.resources",
        "com.google.android.sdksandbox",

        // SO-02K, Android 8.0
        "com.sonyericsson.providers.cnap", // 発信者名表示プロバイダー
        "com.sonymobile.android.contacts", // 連絡先
        "com.nttdocomo.android.wipe", // 遠隔初期化
        "com.sonymobile.android.dialer" // 電話
    )
}
