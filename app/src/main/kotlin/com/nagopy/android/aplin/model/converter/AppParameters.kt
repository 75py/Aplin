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

import android.content.ComponentName
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.entity.AppPermission
import io.realm.RealmList
import timber.log.Timber
import java.util.*

enum class AppParameters(val targetSdkVersion: IntRange) : AppConverter.Converter {
    packageName(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.packageName = params.applicationInfo.packageName
        }
    },
    label(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.label = params.applicationInfo.loadLabel(params.appConverter.packageManager).toString()
        }
    },
    isEnabled(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isEnabled = params.applicationInfo.enabled
        }
    },
    isSystem(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isSystem =
                    (params.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            || (params.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        }
    },
    isThisASystemPackage(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isThisASystemPackage = params.appConverter.aplinDevicePolicyManager.isThisASystemPackage(params.packageInfo)
        }
    },
    firstInstallTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.firstInstallTime = params.packageInfo.firstInstallTime
        }
    },
    lastUpdateTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.lastUpdateTime = params.packageInfo.lastUpdateTime
        }
    },
    hasActiveAdmins(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.hasActiveAdmins = params.appConverter.aplinDevicePolicyManager.packageHasActiveAdmins(params.applicationInfo.packageName)
        }
    },
    isInstalled(IntRange(Build.VERSION_CODES.JELLY_BEAN_MR1, Int.MAX_VALUE)) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isInstalled = (params.applicationInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
        }
    },
    isDefaultApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            val outFilters = ArrayList<IntentFilter>()
            val outActivities = ArrayList<ComponentName>()
            params.appConverter.packageManager.getPreferredActivities(outFilters, outActivities, params.applicationInfo.packageName)
            app.isDefaultApp = !outActivities.isEmpty()
        }
    },
    isHomeApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isHomeApp = params.homeActivities.contains(params.applicationInfo.packageName)
        }
    },
    icon(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            if (params.applicationInfo.icon == 0) {
                Timber.v(params.applicationInfo.packageName + ", icon=0x0")
                app.iconByteArray = params.appConverter.iconHelper.defaultIconByteArray
            } else {
                app.iconByteArray = params.appConverter.iconHelper.toByteArray(params.applicationInfo.loadIcon(
                        params.appConverter.packageManager))
            }
        }
    },
    versionName(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.versionName = params.packageInfo.versionName
        }
    },
    permissions(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.permissions = RealmList()
            params.packageInfo.requestedPermissions?.forEach {
                val permission = params.realm.createObject(AppPermission::class.java)
                permission.name = it
                try {
                    val pi = params.appConverter.packageManager.getPermissionInfo(it, 0)
                    permission.label = pi.loadLabel(params.appConverter.packageManager).toString()
                    permission.group = pi.group
                    params.allPermissionGroups.forEach {
                        if (it.name.equals(pi.group)) {
                            permission.groupLabel = it.loadLabel(params.appConverter.packageManager).toString()
                        }
                    }
                } catch(e: PackageManager.NameNotFoundException) {
                    Timber.d("ignore ${e.message}")
                }
                app.permissions.add(permission)
            }
        }
    },
    isProfileOrDeviceOwner(Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun setValue(app: App, params: AppConverter.Params) {
            app.isProfileOrDeviceOwner = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && params.appConverter.aplinDevicePolicyManager.isProfileOrDeviceOwner(params.applicationInfo.packageName)
        }
    },
    ;

    override fun targetSdkVersion(): IntRange = targetSdkVersion
}