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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PermissionGroupInfo
import android.os.Build
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.DevicePolicy
import com.nagopy.android.aplin.model.IconHelper
import io.realm.Realm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppConverter {

    @Inject
    open lateinit var application: Application

    @Inject
    open lateinit var packageManager: PackageManager

    @Inject
    open lateinit var iconHelper: IconHelper

    @Inject
    open lateinit var devicePolicy: DevicePolicy

    @Inject
    open lateinit var appUsageStatsManager: AppUsageStatsManager

    @Inject
    constructor() {
    }

    open fun setValues(realm: Realm, app: App, applicationInfo: ApplicationInfo) {
        val allPermissionGroups = packageManager.getAllPermissionGroups(0)
        val packageInfo = packageManager.getPackageInfo(
                applicationInfo.packageName,
                PackageManager.GET_PERMISSIONS
                        or PackageManager.GET_META_DATA
                        or PackageManager.GET_DISABLED_COMPONENTS
                        or PackageManager.GET_UNINSTALLED_PACKAGES
                        or PackageManager.GET_SIGNATURES
        )
        val appInfo = AppInfo(applicationInfo, packageInfo)
        val envInfo = EnvInfo(this, realm, allPermissionGroups)
        AppParameters.values
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach { param ->
                    param.setValue(app, appInfo, envInfo)
                }
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(app: App, appInfo: AppInfo, envInfo: EnvInfo)
    }

    open class AppInfo(open val applicationInfo: ApplicationInfo, open val packageInfo: PackageInfo)
    open class EnvInfo(open val appConverter: AppConverter, open val realm: Realm, open val allPermissionGroups: List<PermissionGroupInfo>)

}