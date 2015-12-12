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
import com.nagopy.android.aplin.entity.names.AppNames.*
import com.nagopy.android.kotlinames.contains
import com.nagopy.android.kotlinames.equalTo
import com.nagopy.android.kotlinames.isNotNull
import io.realm.RealmQuery

public enum class Category(
        val titleResourceId: Int
        , val summaryResourceId: Int
        , val targetSdkVersion: IntRange = Constants.ALL_SDK_VERSION) {

    ALL(titleResourceId = R.string.category_all
            , summaryResourceId = R.string.category_all_summary)

    ,
    SYSTEM(
            titleResourceId = R.string.category_system
            , summaryResourceId = R.string.category_system_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isSystem(), true)
        }
    }
    ,
    SYSTEM_UNDISABLABLE(
            titleResourceId = R.string.category_system_undisablable
            , summaryResourceId = R.string.category_system_undisablable_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isSystem(), true)
                    .beginGroup()
                    .equalTo(isThisASystemPackage(), true)
                    .or().equalTo(hasActiveAdmins(), true)
                    .or().equalTo(isProfileOrDeviceOwner(), true)
                    .or().equalTo(isHomeApp(), true)
                    .endGroup()
        }
    }
    ,
    SYSTEM_DISABLABLE(titleResourceId = R.string.category_system_disablable
            , summaryResourceId = R.string.category_system_disablable_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isSystem(), true)
                    .equalTo(isProfileOrDeviceOwner(), false)
                    .equalTo(isThisASystemPackage(), false)
                    .equalTo(hasActiveAdmins(), false)
                    .equalTo(isHomeApp(), false) // システムのHomeアプリは無効化不可
        }
    }
    ,
    DISABLED(titleResourceId = R.string.category_disabled
            , summaryResourceId = R.string.category_disabled_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isEnabled(), false)
        }
    }
    ,
    DEFAULT(titleResourceId = R.string.category_default
            , summaryResourceId = R.string.category_default_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isDefaultApp(), true)
        }
    }
    ,
    USER(titleResourceId = R.string.category_user
            , summaryResourceId = R.string.category_user_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isSystem(), false)
        }
    }
    ,
    INTERNET_PERMISSIONS(titleResourceId = R.string.category_internet_permissions
            , summaryResourceId = R.string.category_internet_permissions_summary) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.contains(permissions().name(), "android.permission.INTERNET")
        }
    }
    ,
    RUNTIME_PERMISSIONS(titleResourceId = R.string.category_runtime_permissions
            , summaryResourceId = R.string.category_runtime_permissions_summary
            , targetSdkVersion = Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery.equalTo(isThisASystemPackage(), false)
                    .isNotNull(permissions().groupLabel())
        }
    }
    ,
    SYSTEM_ALERT_WINDOW_PERMISSION(titleResourceId = R.string.category_system_alert_window_permission
            , summaryResourceId = R.string.category_system_alert_window_permission_summary
            , targetSdkVersion = Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun where(realmQuery: RealmQuery<App>): RealmQuery<App> {
            return realmQuery
                    .equalTo(isThisASystemPackage(), false)
                    .contains(permissions().name(), android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        }
    }
    ,
    ;

    open fun where(realmQuery: RealmQuery<App>): RealmQuery<App> = realmQuery

    companion object {
        fun getAll() = Category.values().filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
    }
}
