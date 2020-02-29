/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.aplin.model

import android.app.admin.DevicePolicyManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.IBinder
import android.print.PrintManager
import android.webkit.IWebViewUpdateService
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties

@Singleton
open class AplinDevicePolicyManager @Inject constructor() {

    @Inject
    lateinit var devicePolicyManager: DevicePolicyManager

    @Inject
    lateinit var packageManager: PackageManager

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
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getPermissionControllerPackageName"
                }?.call(packageManager) as? String
                Timber.d("permissionControllerPackageName = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "getPermissionControllerPackageName の実行に失敗")
            }
        }
        return@lazy null
    }

    val servicesSystemSharedLibraryPackageName: String? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getServicesSystemSharedLibraryPackageName"
                }?.call(packageManager) as? String
                Timber.d("servicesSystemSharedLibraryPackageName = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "getServicesSystemSharedLibraryPackageName の実行に失敗")
            }
        }
        return@lazy null
    }

    val sharedSystemSharedLibraryPackageName: String? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            try {
                val v = PackageManager::class.declaredMemberFunctions.firstOrNull {
                    it.name == "getSharedSystemSharedLibraryPackageName"
                }?.call(packageManager) as? String
                Timber.d("sharedSystemSharedLibraryPackageName = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "getSharedSystemSharedLibraryPackageName の実行に失敗")
            }
        }
        return@lazy null
    }

    val PRINT_SPOOLER_PACKAGE_NAME: String? by lazy {
        if (Build.VERSION_CODES.N_MR1 <= Build.VERSION.SDK_INT) {
            try {
                val v = PrintManager::class.staticProperties.firstOrNull {
                    it.name == "PRINT_SPOOLER_PACKAGE_NAME"
                }?.call() as? String
                Timber.d("PRINT_SPOOLER_PACKAGE_NAME = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "PRINT_SPOOLER_PACKAGE_NAME の取得に失敗")
            }
        }
        return@lazy null
    }

    val deviceProvisioningPackage: String? by lazy {
        if (Build.VERSION_CODES.N_MR1 <= Build.VERSION.SDK_INT) {
            try {
                val r = Resources.getSystem()
                val id = r.getIdentifier("config_deviceProvisioningPackage", "string", "android")
                val v = r.getString(id)
                Timber.d("deviceProvisioningPackage id = %d, value = %s", id, v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "deviceProvisioningPackage の取得に失敗")
            }
        }
        return@lazy null
    }

    val webviewUpdateService: IWebViewUpdateService? by lazy {
        if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // 7.0 <= version < 10
            try {
                val clsServiceManager = Class.forName("android.os.ServiceManager").kotlin
                val clsServiceManager_getService = clsServiceManager.staticFunctions.first {
                    it.name == "getService" && it.parameters.size == 1
                }
                val ibinder = clsServiceManager_getService.call("webviewupdate") as? IBinder
                val v = IWebViewUpdateService.Stub.asInterface(ibinder)
                Timber.d("webviewUpdateService = %s", v)
                return@lazy v
            } catch (e: Exception) {
                Timber.e(e, "webviewUpdateService の取得に失敗")
            }
        }
        return@lazy null
    }

    /**
     * [DevicePolicyManager]のpackageHasActiveAdminsメソッドを実行する

     * @param packageName パッケージ名
     * *
     * @return packageHasActiveAdminsの結果を返す。
     * * エラーがあった場合はfalseを返す。
     */
    open fun packageHasActiveAdmins(packageName: String): Boolean {
        try {
            return packageHasActiveAdmins?.invoke(devicePolicyManager, packageName) as Boolean
        } catch (e: IllegalAccessException) {
            Timber.e(e, "実行失敗")
        } catch (e: InvocationTargetException) {
            Timber.e(e, "実行失敗")
        }
        return false
    }

    open fun isSystemPackage(packageInfo: PackageInfo?): Boolean = when {
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.M -> isThisASystemPackage(packageInfo)
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.N ->
            // 7.0
            isSystemPackageApi24(packageInfo)
        else ->
            // 7.1-
            isSystemPackageApi25(packageInfo)
    }

    /**
     * [DevicePolicyManager]のisThisASystemPackageメソッドと同じ内容.
     * 4.4以下で使用。

     * @param packageInfo 判定したいpackageInfo
     * *
     * @return isThisASystemPackageの結果をそのまま返す。
     * * エラーがあった場合はfalseを返す。
     */
    open fun isThisASystemPackage(packageInfo: PackageInfo?): Boolean {
        return (packageInfo?.signatures != null
                && mSystemPackageInfo != null
                && mSystemPackageInfo!!.signatures[0] == packageInfo.signatures[0])
    }

    // Utils#isSystemPackage
    open fun isSystemPackageApi24(packageInfo: PackageInfo?): Boolean {
        return packageInfo != null
                && (isThisASystemPackage(packageInfo)
                || packageInfo.packageName == permissionControllerPackageName
                || packageInfo.packageName == servicesSystemSharedLibraryPackageName
                || packageInfo.packageName == sharedSystemSharedLibraryPackageName
                || isFallbackPackage(webviewUpdateService, packageInfo.packageName)
                )
    }

    fun isFallbackPackage(webviewUpdateService: IWebViewUpdateService?, packageName: String): Boolean {
        return try {
            webviewUpdateService?.isFallbackPackage(packageName) ?: false
        } catch (e: Error) {
            Timber.d(e)
            false
        }
    }

    // Utils#isSystemPackage
    open fun isSystemPackageApi25(packageInfo: PackageInfo?): Boolean {
        return packageInfo != null
                && (
                isSystemPackageApi24(packageInfo)
                        || packageInfo.packageName == PRINT_SPOOLER_PACKAGE_NAME
                        || packageInfo.packageName == deviceProvisioningPackage
                )
    }

    open fun isProfileOrDeviceOwner(packageName: String): Boolean = devicePolicyManager.isDeviceOwnerApp(packageName)
            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && devicePolicyManager.isProfileOwnerApp(packageName))

}