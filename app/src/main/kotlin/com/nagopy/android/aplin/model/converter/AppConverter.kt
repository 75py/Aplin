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
import android.content.pm.PermissionGroupInfo
import android.os.Build
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.AplinDevicePolicyManager
import com.nagopy.android.aplin.model.IconHelper
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

    lateinit var allPermissionGroups: List<PermissionGroupInfo>
    lateinit var homeActivities: List<String>
    lateinit var launcherPkgs: List<String>
    val enabledSettingField: Field = ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
        isAccessible = true
    }

    open fun prepare() {
        allPermissionGroups = packageManager.getAllPermissionGroups(0)
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        homeActivities = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
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

    open fun setValues(app: App, applicationInfo: ApplicationInfo, vararg appParameters: AppParameters = AppParameters.values()) {
        try {
            val params = prepareForApp(applicationInfo)
            appParameters
                    .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                    .forEach { param ->
                        param.setValue(app, params)
                    }
        } catch(e: PackageManager.NameNotFoundException) {
            Timber.w(e, "Not found. pkg=%s", applicationInfo.packageName)
        } catch(e: Exception) {
            Timber.e(e, "Error occurred. pkg=%s", applicationInfo.packageName)
        }
    }

    open fun prepareForApp(applicationInfo: ApplicationInfo): Params {
        val packageInfo = packageManager.getPackageInfo(
                applicationInfo.packageName,
                PackageManager.GET_PERMISSIONS
                        or PackageManager.GET_META_DATA
                        or PackageManager.GET_DISABLED_COMPONENTS
                        or PackageManager.GET_UNINSTALLED_PACKAGES
                        or PackageManager.GET_SIGNATURES
        )
        return Params(applicationInfo, packageInfo, allPermissionGroups, homeActivities, launcherPkgs, enabledSettingField, this)
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(app: App, params: Params)
    }

    // dataクラスにしないのは、Mockitoで置き換えるため
    open class Params(open var applicationInfo: ApplicationInfo
                      , open var packageInfo: PackageInfo
                      , open var allPermissionGroups: List<PermissionGroupInfo>
                      , open var homeActivities: List<String>
                      , open var launcherPkgs: List<String>
                      , open var enabledSettingField: Field
                      , open var appConverter: AppConverter
    )

}
