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
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.packageName = appInfo.applicationInfo.packageName
        }
    },
    label(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.label = appInfo.applicationInfo.loadLabel(envInfo.appConverter.packageManager).toString()
        }
    },
    isEnabled(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.isEnabled = appInfo.applicationInfo.enabled
        }
    },
    isSystem(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.isSystem =
                    (appInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            || (appInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        }
    },
    isThisASystemPackage(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.isThisASystemPackage = envInfo.appConverter.devicePolicy.isThisASystemPackage(appInfo.packageInfo)
        }
    },
    firstInstallTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.firstInstallTime = appInfo.packageInfo.firstInstallTime
        }
    },
    lastUpdateTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.lastUpdateTime = appInfo.packageInfo.lastUpdateTime
        }
    },
    hasActiveAdmins(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.hasActiveAdmins = envInfo.appConverter.devicePolicy.packageHasActiveAdmins(appInfo.applicationInfo.packageName)
        }
    },
    isInstalled(IntRange(Build.VERSION_CODES.JELLY_BEAN_MR1, Int.MAX_VALUE)) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.isInstalled = (appInfo.applicationInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
        }
    },
    isDefaultApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            val outFilters = ArrayList<IntentFilter>()
            val outActivities = ArrayList<ComponentName>()
            envInfo.appConverter.packageManager.getPreferredActivities(outFilters, outActivities, appInfo.applicationInfo.packageName)
            app.isDefaultApp = !outActivities.isEmpty()
        }
    },
    icon(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            if (appInfo.applicationInfo.icon == 0) {
                Timber.v(appInfo.applicationInfo.packageName + ", icon=0x0")
                app.iconByteArray = envInfo.appConverter.iconHelper.defaultIconByteArray
            } else {
                app.iconByteArray = envInfo.appConverter.iconHelper.toByteArray(appInfo.applicationInfo.loadIcon(
                        envInfo.appConverter.packageManager))
            }
        }
    },
    lastTimeUsed(Build.VERSION_CODES.LOLLIPOP..Int.MAX_VALUE) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            val times = envInfo.appConverter.appUsageStatsManager.getLaunchTimes().get(appInfo.applicationInfo.packageName)
            if (times != null) {
                app.launchTimes = times
            }
        }
    },
    versionName(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.versionName = appInfo.packageInfo.versionName
        }
    },
    permissions(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, appInfo: AppConverter.AppInfo, envInfo: AppConverter.EnvInfo) {
            app.permissions = RealmList()
            appInfo.packageInfo.requestedPermissions?.forEach {
                val permission = envInfo.realm.createObject(AppPermission::class.java)
                permission.name = it
                try {
                    val pi = envInfo.appConverter.packageManager.getPermissionInfo(it, 0)
                    permission.label = pi.loadLabel(envInfo.appConverter.packageManager).toString()
                    permission.group = pi.group
                    envInfo.allPermissionGroups.forEach {
                        if (it.name.equals(pi.group)) {
                            permission.groupLabel = it.loadLabel(envInfo.appConverter.packageManager).toString()
                        }
                    }
                } catch(e: PackageManager.NameNotFoundException) {
                    Timber.d("ignore ${e.message}")
                }
                app.permissions.add(permission)
            }
        }
    }
    ;

    override fun targetSdkVersion(): IntRange = targetSdkVersion
}