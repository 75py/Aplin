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

import android.os.Build
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App

enum class Category(
        val titleResourceId: Int, val summaryResourceId: Int, val targetSdkVersion: IntRange = Constants.ALL_SDK_VERSION) {

    ALL(titleResourceId = R.string.category_all, summaryResourceId = R.string.category_all_summary) {
        override fun filter(list: Collection<App>): Collection<App> = list
    },
    SYSTEM(
            titleResourceId = R.string.category_system, summaryResourceId = R.string.category_system_summary) {
        override fun filter(list: Collection<App>): Collection<App> = list.filter(App::isSystem)
    },
    SYSTEM_UNDISABLABLE(
            titleResourceId = R.string.category_system_undisablable, summaryResourceId = R.string.category_system_undisablable_summary) {
        override fun filter(list: Collection<App>): Collection<App> {
            return list.filter {
                it.isSystem && (it.isSystemPackage || it.hasActiveAdmins || it.isProfileOrDeviceOwner || it.isHomeApp
                        || it.isResourceOverlay)
            }
        }
    },
    SYSTEM_DISABLABLE(titleResourceId = R.string.category_system_disablable, summaryResourceId = R.string.category_system_disablable_summary) {
        override fun filter(list: Collection<App>): Collection<App> {
            return list.filter {
                it.isSystem && !it.isProfileOrDeviceOwner && !it.isSystemPackage && !it.hasActiveAdmins && !it.isHomeApp
                        && !it.isResourceOverlay
            }
        }
    },
    DISABLED(titleResourceId = R.string.category_disabled, summaryResourceId = R.string.category_disabled_summary) {
        override fun filter(list: Collection<App>): Collection<App> = list.filter { !it.isEnabled }
    },
    DEFAULT(titleResourceId = R.string.category_default, summaryResourceId = R.string.category_default_summary) {
        override fun filter(list: Collection<App>): Collection<App> = list.filter(App::isDefaultApp)
    },
    USER(titleResourceId = R.string.category_user, summaryResourceId = R.string.category_user_summary) {
        override fun filter(list: Collection<App>): Collection<App> = list.filter { !it.isSystem }
    },
    INTERNET_PERMISSIONS(titleResourceId = R.string.category_internet_permissions, summaryResourceId = R.string.category_internet_permissions_summary) {
        override fun filter(list: Collection<App>): Collection<App> =
                list.filter { it.requestedPermissions.contains(android.Manifest.permission.INTERNET) }
    },
    DENIABLE_PERMISSIONS(titleResourceId = R.string.category_deniable_permissions, summaryResourceId = R.string.category_deniable_permissions_summary, targetSdkVersion = Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun filter(list: Collection<App>): Collection<App> {
            return list.filter {
                !it.isSystemPackage
                        && it.permissionGroups.isNotEmpty()
            }
        }
    },
    SYSTEM_ALERT_WINDOW_PERMISSION(titleResourceId = R.string.category_system_alert_window_permission, summaryResourceId = R.string.category_system_alert_window_permission_summary, targetSdkVersion = Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun filter(list: Collection<App>): Collection<App> {
            return list.filter {
                !it.isSystemPackage
                        && it.requestedPermissions.contains(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
        }
    },
    ;

    abstract fun filter(list: Collection<App>): Collection<App>

    fun where(list: Collection<App>): Collection<App> = filter(list).filter { !it.shouldSkip }

    companion object {
        fun getAll() = values().filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
    }
}
