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

package com.nagopy.android.aplin.model.converter

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.entity.PermissionGroup
import com.nagopy.android.aplin.model.AplinDevicePolicyManager
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.model.PermissionGroups
import timber.log.Timber
import java.lang.reflect.Field
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppConverter @Inject constructor() {

    @Inject
    open lateinit var application: Application

    @Inject
    open lateinit var packageManager: PackageManager

    @Inject
    open lateinit var iconHelper: IconHelper

    @Inject
    open lateinit var aplinDevicePolicyManager: AplinDevicePolicyManager

    @Inject
    lateinit var permissionGroups: PermissionGroups

    lateinit var allPermissionGroups: List<PermissionGroup>
    lateinit var homeActivities: List<String>
    lateinit var launcherPkgs: List<String>
    val enabledSettingField: Field = ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
        isAccessible = true
    }

    open fun prepare() {
        allPermissionGroups = permissionGroups.getAllPermissionGroups()
        Timber.v("allPermissionGroups %s", allPermissionGroups)

        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        homeActivities = packageManager.queryIntentActivities(intent, 0)
                .map { it.activityInfo.packageName }
                .plus("com.google.android.launcher") // 仕組みが未確認だが、これはホームアプリ判定になっているっぽい

        val launcherIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        launcherPkgs =
                try {
                    packageManager.queryIntentActivities(launcherIntent, 0).map { it.activityInfo.packageName }
                } catch(e: Exception) {
                    Timber.w(e, "Error: queryIntentActivities")
                    emptyList<String>()
                }
    }

    open fun setValues(app: App, packageName: String, vararg appParameters: AppParameters = AppParameters.values()) {
        try {
            val pi = getPackageInfo(packageName)
            appParameters
                    .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                    .forEach { param ->
                        param.setValue(app, pi, this@AppConverter)
                    }
        } catch(e: PackageManager.NameNotFoundException) {
            Timber.w(e, "Not found. pkg=%s", packageName)
        } catch(e: Exception) {
            Timber.e(e, "Error occurred. pkg=%s", packageName)
        }
    }

    fun getPackageInfo(packageName: String): PackageInfo {
        // 一度に取得する量が多いとエラーになるらしいので、細かく分けて取得する
        val packageInfo = packageManager.getPackageInfo(
                packageName,
                Applications.flags
        )

        try {
            val pkg = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS or Applications.flags
            )
            packageInfo.requestedPermissions = pkg.requestedPermissions
        } catch (e: Exception) {
            Timber.w(e)
        }

        try {
            val sig = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES or Applications.flags
            )
            packageInfo.signatures = sig.signatures
        } catch (e: Exception) {
            Timber.w(e)
        }

        return packageInfo
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter)
    }

}
