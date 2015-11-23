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
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class DevicePolicy
@Inject
constructor(application: Application) {

    val devicePolicyManager: DevicePolicyManagerWrapper = DevicePolicyManagerWrapper(application)

    public fun isThisASystemPackage(packageInfo: PackageInfo): Boolean {
        return devicePolicyManager.isThisASystemPackage(packageInfo)
    }

    public fun packageHasActiveAdmins(packageName: String): Boolean {
        return devicePolicyManager.packageHasActiveAdmins(packageName)
    }

    class DevicePolicyManagerWrapper
    constructor(context: Context) {

        val devicePolicyManager: DevicePolicyManager
        val mSystemPackageInfo: PackageInfo?
        val packageHasActiveAdmins: Method?

        init {
            devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            try {
                packageHasActiveAdmins = DevicePolicyManager::class.java.getDeclaredMethod("packageHasActiveAdmins", String::class.java)
            } catch (e: NoSuchMethodException) {
                Timber.e(e, "リフレクション失敗")
                packageHasActiveAdmins = null
            }

            try {
                mSystemPackageInfo = context.packageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e, "システムのシグネチャ取得に失敗")
                mSystemPackageInfo = null
            }
        }

        /**
         * [DevicePolicyManager]のpackageHasActiveAdminsメソッドを実行する

         * @param packageName パッケージ名
         * *
         * @return packageHasActiveAdminsの結果を返す。
         * * エラーがあった場合はfalseを返す。
         */
        public fun packageHasActiveAdmins(packageName: String): Boolean {
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
        fun isThisASystemPackage(packageInfo: PackageInfo?): Boolean {
            return (packageInfo != null
                    && packageInfo.signatures != null
                    && mSystemPackageInfo != null
                    && mSystemPackageInfo.signatures[0] == packageInfo.signatures[0])
        }
    }
}
