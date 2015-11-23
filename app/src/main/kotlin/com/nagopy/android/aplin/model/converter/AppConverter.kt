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
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    lateinit var iconHelper: IconHelper

    @Inject
    lateinit var devicePolicy: DevicePolicy

    @Inject
    lateinit var appUsageStatsManager: AppUsageStatsManager

    lateinit var allPermissionGroups: List<PermissionGroupInfo>

    @Inject
    constructor() {
    }

    open fun setValues(realm: Realm, app: App, applicationInfo: ApplicationInfo) {
        allPermissionGroups = packageManager.getAllPermissionGroups(0)
        AppParameters.values
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach { param ->
                    param.setValue(realm, app, applicationInfo, this)
                }
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(realm: Realm, entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter)
    }

}