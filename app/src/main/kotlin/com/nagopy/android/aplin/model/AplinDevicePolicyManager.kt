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

import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AplinDevicePolicyManager {


    @Inject
    lateinit var application: Application

    @Inject
    lateinit var devicePolicyManager: DevicePolicyManager

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    constructor()

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

    /**
     * [DevicePolicyManager]のisThisASystemPackageメソッドと同じ内容.
     * 4.4以下で使用。

     * @param packageInfo 判定したいpackageInfo
     * *
     * @return isThisASystemPackageの結果をそのまま返す。
     * * エラーがあった場合はfalseを返す。
     */
    open fun isThisASystemPackage(packageInfo: PackageInfo?): Boolean {
        return (packageInfo != null
                && packageInfo.signatures != null
                && mSystemPackageInfo != null
                && mSystemPackageInfo!!.signatures[0] == packageInfo.signatures[0])
    }

    open fun isProfileOrDeviceOwner(packageName: String): Boolean
            = devicePolicyManager.isDeviceOwnerApp(packageName) || devicePolicyManager.isProfileOwnerApp(packageName)

}