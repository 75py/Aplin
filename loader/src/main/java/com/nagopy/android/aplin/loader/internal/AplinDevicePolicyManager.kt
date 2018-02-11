package com.nagopy.android.aplin.loader.internal

import android.app.admin.DevicePolicyManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.print.PrintManager
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

internal class AplinDevicePolicyManager(
        val packageManager: PackageManager
        , val devicePolicyManager: DevicePolicyManager) {

    val mSystemPackageInfo: PackageInfo? by lazy {
        try {
            packageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "システムのシグネチャ取得に失敗")
            null
        }
    }

    val packageHasActiveAdmins: Method? by lazy {
        try {
            DevicePolicyManager::class.java.getDeclaredMethod("packageHasActiveAdmins", String::class.java)
        } catch (e: NoSuchMethodException) {
            Timber.e(e, "リフレクション失敗")
            null
        }
    }

    val permissionControllerPackageName: String? by lazy {
        // 7.0-
        try {
            val v = PackageManager::class.java.declaredMethods.firstOrNull {
                it.name == "getPermissionControllerPackageName"
            }?.invoke(packageManager) as? String
            Timber.d("permissionControllerPackageName = %s", v)
            return@lazy v
        } catch (e: Exception) {
            Timber.e(e, "getPermissionControllerPackageName の実行に失敗")
            return@lazy null
        }
    }

    val servicesSystemSharedLibraryPackageName: String? by lazy {
        // 7.0-
        try {
            val v = PackageManager::class.java.declaredMethods.firstOrNull {
                it.name == "getServicesSystemSharedLibraryPackageName"
            }?.invoke(packageManager) as? String
            Timber.d("servicesSystemSharedLibraryPackageName = %s", v)
            return@lazy v
        } catch (e: Exception) {
            Timber.e(e, "getServicesSystemSharedLibraryPackageName の実行に失敗")
            return@lazy null
        }
    }

    val sharedSystemSharedLibraryPackageName: String? by lazy {
        // 7.0-
        try {
            val v = PackageManager::class.java.declaredMethods.firstOrNull {
                it.name == "getSharedSystemSharedLibraryPackageName"
            }?.invoke(packageManager) as? String
            Timber.d("sharedSystemSharedLibraryPackageName = %s", v)
            return@lazy v
        } catch (e: Exception) {
            Timber.e(e, "getSharedSystemSharedLibraryPackageName の実行に失敗")
            return@lazy null
        }
    }

    val PRINT_SPOOLER_PACKAGE_NAME: String? by lazy {
        if (Build.VERSION_CODES.N_MR1 <= Build.VERSION.SDK_INT) {
            try {
                val v = PrintManager::class.java.getDeclaredField("PRINT_SPOOLER_PACKAGE_NAME")
                        .get(null) as? String
                Timber.d("PRINT_SPOOLER_PACKAGE_NAME = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "PRINT_SPOOLER_PACKAGE_NAME の取得に失敗")
            }
        }
        return@lazy null
    }

    val deviceProvisioningPackage: String? by lazy {
        // 7.1-
        try {
            val r = Resources.getSystem()
            val id = r.getIdentifier("config_deviceProvisioningPackage", "string", "android")
            val v = r.getString(id)
            Timber.d("deviceProvisioningPackage id = %d, value = %s", id, v)
            return@lazy v
        } catch (e: Exception) {
            Timber.e(e, "deviceProvisioningPackage の取得に失敗")
            return@lazy null
        }
    }

    /**
     * [DevicePolicyManager]のpackageHasActiveAdminsメソッドを実行する
     * @param packageName パッケージ名
     * *
     * @return packageHasActiveAdminsの結果を返す。
     * * エラーがあった場合はfalseを返す。
     */
    fun packageHasActiveAdmins(packageName: String): Boolean {
        try {
            return packageHasActiveAdmins?.invoke(devicePolicyManager, packageName) as Boolean
        } catch (e: IllegalAccessException) {
            Timber.e(e, "実行失敗")
        } catch (e: InvocationTargetException) {
            Timber.e(e, "実行失敗")
        }
        return false
    }

    fun isSystemPackage(packageInfo: PackageInfo?): Boolean =
            isSystemPackageApi25(packageInfo)

    /**
     * [DevicePolicyManager]のisThisASystemPackageメソッドと同じ内容.
     * 4.4以下で使用。
     * @param packageInfo 判定したいpackageInfo
     * *
     * @return isThisASystemPackageの結果をそのまま返す。
     * * エラーがあった場合はfalseを返す。
     */
    fun isThisASystemPackage(packageInfo: PackageInfo?): Boolean {
        return (packageInfo?.signatures != null
                && packageInfo.signatures.isNotEmpty()
                && mSystemPackageInfo != null
                && mSystemPackageInfo!!.signatures.isNotEmpty()
                && mSystemPackageInfo!!.signatures[0] == packageInfo.signatures[0])
    }

    // Utils#isSystemPackage
    fun isSystemPackageApi24(packageInfo: PackageInfo?): Boolean {
        return packageInfo != null
                && (isThisASystemPackage(packageInfo)
                || packageInfo.packageName == permissionControllerPackageName
                || packageInfo.packageName == servicesSystemSharedLibraryPackageName
                || packageInfo.packageName == sharedSystemSharedLibraryPackageName
                )
    }

    // Utils#isSystemPackage
    fun isSystemPackageApi25(packageInfo: PackageInfo?): Boolean {
        return packageInfo != null
                && (
                isSystemPackageApi24(packageInfo)
                        || packageInfo.packageName == PRINT_SPOOLER_PACKAGE_NAME
                        || packageInfo.packageName == deviceProvisioningPackage
                )
    }

    fun isProfileOrDeviceOwner(packageName: String): Boolean = devicePolicyManager.isDeviceOwnerApp(packageName)
            ||
            ( // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    devicePolicyManager.isProfileOwnerApp(packageName)
                    )

}
