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
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import java.util.*

enum class AppParameters(val targetSdkVersion: IntRange) : AppConverter.Converter {
    packageName(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.packageName = packageInfo.applicationInfo.packageName
        }
    },
    label(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.label = packageInfo.applicationInfo.loadLabel(appConverter.packageManager).toString()
        }
    },
    isEnabled(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isEnabled = packageInfo.applicationInfo.enabled
        }
    },
    isSystem(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isSystem =
                    (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            || (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        }
    },
    isSystemPackage(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isSystemPackage = appConverter.aplinDevicePolicyManager.isSystemPackage(packageInfo)
        }
    },
    firstInstallTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.firstInstallTime = packageInfo.firstInstallTime
        }
    },
    lastUpdateTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.lastUpdateTime = packageInfo.lastUpdateTime
        }
    },
    hasActiveAdmins(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.hasActiveAdmins = appConverter.aplinDevicePolicyManager.packageHasActiveAdmins(packageInfo.applicationInfo.packageName)
        }
    },
    isInstalled(IntRange(Build.VERSION_CODES.JELLY_BEAN_MR1, Int.MAX_VALUE)) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isInstalled = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
        }
    },
    isDefaultApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            val outFilters = ArrayList<IntentFilter>()
            val outActivities = ArrayList<ComponentName>()
            appConverter.packageManager.getPreferredActivities(outFilters, outActivities, packageInfo.applicationInfo.packageName)
            app.isDefaultApp = !outActivities.isEmpty()
        }
    },
    isHomeApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isHomeApp = appConverter.homeActivities.contains(packageInfo.applicationInfo.packageName)
        }
    },
    versionName(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.versionName = packageInfo.versionName
        }
    },
    requestedPermissions(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            packageInfo.requestedPermissions?.forEach {
                app.requestedPermissions.add(it)
            }
        }
    },
    permissionGroups(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            packageInfo.requestedPermissions?.map { p ->
                appConverter.allPermissionGroups.filter { it.permissions.contains(p) }
            }?.flatMap { it }
                    ?.distinct()
                    ?.sortedBy { it.label }
                    ?.forEach {
                        app.permissionGroups.add(it)
                    }
        }
    },
    isProfileOrDeviceOwner(Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isProfileOrDeviceOwner = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && appConverter.aplinDevicePolicyManager.isProfileOrDeviceOwner(packageInfo.applicationInfo.packageName)
        }
    },
    isLaunchable(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.isLaunchable = appConverter.launcherPkgs.contains(packageInfo.applicationInfo.packageName)
        }
    },
    isDisabledUntilUsed(Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 6.0-
                val enabledSetting = appConverter.enabledSettingField.get(packageInfo.applicationInfo)
                app.isDisabledUntilUsed = enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
            }
        }
    },
    shouldSkip(Constants.ALL_SDK_VERSION) {
        override fun setValue(app: App, packageInfo: PackageInfo, appConverter: AppConverter) {
            app.shouldSkip = false
            if (packageInfo.applicationInfo.packageName.isEmpty()) {
                app.shouldSkip = true
            } else if (!packageInfo.applicationInfo.enabled) {
                // 無効になっていて、かつenabledSettingが3でないアプリは除外する
                val enabledSetting = appConverter.enabledSettingField.get(packageInfo.applicationInfo)
                if (enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                    app.shouldSkip = true
                }
            }
        }
    },
    ;

    override fun targetSdkVersion(): IntRange = targetSdkVersion
}