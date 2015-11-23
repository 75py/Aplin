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
import timber.log.Timber
import java.util.*

enum class AppParameters(val targetSdkVersion: IntRange) : AppConverter.Converter {
    packageName(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.packageName = applicationInfo.packageName
        }
    },
    label(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.label = applicationInfo.loadLabel(appConverter.packageManager).toString()
        }
    },
    isEnabled(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.isEnabled = applicationInfo.enabled
        }
    },
    isSystem(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.isSystem =
                    (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                            || (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        }
    },
    isThisASystemPackage(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val packageInfo = appConverter.packageManager.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_DISABLED_COMPONENTS or PackageManager.GET_UNINSTALLED_PACKAGES or PackageManager.GET_SIGNATURES
            )
            entity.isThisASystemPackage = appConverter.devicePolicy.isThisASystemPackage(packageInfo)
        }
    },
    firstInstallTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val packageInfo = appConverter.packageManager.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_META_DATA
            )
            entity.firstInstallTime = packageInfo.firstInstallTime
        }
    },
    lastUpdateTime(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val packageInfo = appConverter.packageManager.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_META_DATA
            )
            entity.lastUpdateTime = packageInfo.lastUpdateTime
        }
    },
    hasActiveAdmins(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.hasActiveAdmins = appConverter.devicePolicy.packageHasActiveAdmins(applicationInfo.packageName)
        }
    },
    isInstalled(IntRange(Build.VERSION_CODES.JELLY_BEAN_MR1, Int.MAX_VALUE)) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            entity.isInstalled = (applicationInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
        }
    },
    isDefaultApp(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val outFilters = ArrayList<IntentFilter>()
            val outActivities = ArrayList<ComponentName>()
            appConverter.packageManager.getPreferredActivities(outFilters, outActivities, applicationInfo.packageName)
            entity.isDefaultApp = !outActivities.isEmpty()
        }
    },
    icon(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            if (applicationInfo.icon == 0) {
                Timber.v(applicationInfo.packageName + ", icon=0x0")
                entity.iconByteArray = appConverter.iconHelper.defaultIconByteArray
            } else {
                entity.iconByteArray = appConverter.iconHelper.toByteArray(applicationInfo.loadIcon(appConverter.packageManager))
            }
        }
    },
    lastTimeUsed(Build.VERSION_CODES.LOLLIPOP..Int.MAX_VALUE) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val times = appConverter.appUsageStatsManager.getLaunchTimes().get(applicationInfo.packageName)
            if (times != null) {
                entity.launchTimes = times
            }
        }
    },
    versionName(Constants.ALL_SDK_VERSION) {
        override fun setValue(entity: App, applicationInfo: ApplicationInfo, appConverter: AppConverter) {
            val packageInfo = appConverter.packageManager.getPackageInfo(
                    applicationInfo.packageName,
                    PackageManager.GET_META_DATA
            )
            entity.versionName = packageInfo.versionName
        }
    }
    ;

    override fun targetSdkVersion(): IntRange = targetSdkVersion
}