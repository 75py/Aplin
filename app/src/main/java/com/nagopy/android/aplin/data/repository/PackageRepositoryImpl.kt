package com.nagopy.android.aplin.data.repository

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.print.PrintManager
import logcat.LogPriority
import logcat.logcat
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.staticProperties

class PackageRepositoryImpl(
    private val packageManager: PackageManager
) : PackageRepository {

    companion object {
        private val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }
    }

    override suspend fun loadAll(): List<PackageInfo> {
        // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/packages/SettingsLib/src/com/android/settingslib/applications/ApplicationsState.java;drc=8cb6c27e8e2f171a0d9f9c4580092ebc4ce562fa

        val hiddenModules = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            packageManager.getInstalledModules(0)
                .map { it.packageName }
                .toHashSet()
        } else {
            emptySet()
        }
        logcat(LogPriority.VERBOSE) { "loadAll() hiddenmodules = $hiddenModules" }

        val retrieveFlags = PackageManager.MATCH_DISABLED_COMPONENTS or
            PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
        val apps = packageManager.getInstalledApplications(retrieveFlags)
            .filterNot { hiddenModules.contains(it.packageName) }
            .mapNotNull {
                logcat(LogPriority.VERBOSE) { "loadAll() ${it.packageName}" }
                try {
                    packageManager.getPackageInfo(it.packageName, flags)
                } catch (e: PackageManager.NameNotFoundException) {
                    logcat(LogPriority.WARN) { "error: $e" }
                    null
                }
            }
        return apps
    }

    override suspend fun loadHomePackageNames(): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val pkgs = packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
        logcat(LogPriority.VERBOSE) { "loadHomePackageNames = $pkgs" }
        return pkgs.toHashSet()
    }

    override suspend fun loadCurrentDefaultHomePackageName(): String? {
        val res = packageManager.resolveActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
            PackageManager.MATCH_DEFAULT_ONLY
        )
        logcat(LogPriority.VERBOSE) { "loadCurrentDefaultHome = ${res?.activityInfo?.applicationInfo?.packageName}" }
        return res?.activityInfo?.applicationInfo?.packageName
    }

    override fun loadLabel(applicationInfo: ApplicationInfo): String {
        return applicationInfo.loadLabel(packageManager).toString()
    }

    override fun loadIcon(applicationInfo: ApplicationInfo): Drawable {
        return applicationInfo.loadIcon(packageManager)
    }

    override val systemPackage: PackageInfo? by lazy {
        try {
            packageManager.getPackageInfo("android", flags)
        } catch (t: Throwable) {
            logcat(LogPriority.VERBOSE) { "mSystemPackageInfo: $t" }
            null
        }
    }

    override val permissionControllerPackageName: String? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getPermissionControllerPackageName"
                }?.call(packageManager) as? String
                logcat(LogPriority.VERBOSE) { "permissionControllerPackageName = $v" }
                return@lazy v
            } catch (t: Throwable) {
                logcat(LogPriority.VERBOSE) { "permissionControllerPackageName: $t" }
            }
        }
        return@lazy null
    }

    override val servicesSystemSharedLibraryPackageName: String? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getServicesSystemSharedLibraryPackageName"
                }?.call(packageManager) as? String
                logcat(LogPriority.VERBOSE) { "servicesSystemSharedLibraryPackageName = $v" }
                return@lazy v
            } catch (t: Throwable) {
                logcat(LogPriority.VERBOSE) { "servicesSystemSharedLibraryPackageName: $t" }
            }
        }
        return@lazy null
    }

    override val sharedSystemSharedLibraryPackageName: String? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getSharedSystemSharedLibraryPackageName"
                }?.call(packageManager) as? String
                logcat(LogPriority.VERBOSE) { "sharedSystemSharedLibraryPackageName = $v" }
                return@lazy v
            } catch (t: Throwable) {
                logcat(LogPriority.VERBOSE) { "sharedSystemSharedLibraryPackageName: $t" }
            }
        }
        return@lazy null
    }

    override val printSpoolerPackageName: String? by lazy {
        try {
            val v = PrintManager::class.staticProperties.firstOrNull {
                it.name == "PRINT_SPOOLER_PACKAGE_NAME"
            }?.call() as? String
            logcat(LogPriority.VERBOSE) { "PRINT_SPOOLER_PACKAGE_NAME = $v" }
            return@lazy v ?: "com.android.printspooler"
        } catch (t: Throwable) {
            logcat(LogPriority.VERBOSE) { "PRINT_SPOOLER_PACKAGE_NAME: $t" }
            return@lazy "com.android.printspooler"
        }
    }

    override val deviceProvisioningPackage: String? by lazy {
        try {
            val r = Resources.getSystem()
            val id = r.getIdentifier("config_deviceProvisioningPackage", "string", "android")
            val v = r.getString(id)
            logcat(LogPriority.VERBOSE) { "deviceProvisioningPackage = $v" }
            return@lazy v
        } catch (t: Throwable) {
            logcat(LogPriority.VERBOSE) { "deviceProvisioningPackage: $t" }
            return@lazy null
        }
    }
}
