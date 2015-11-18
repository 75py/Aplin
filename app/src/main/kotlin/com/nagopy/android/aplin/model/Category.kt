/*
 * Copyright (C) 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagopy.android.aplin.model

import android.content.Context
import android.os.Build
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.entity.names.AppEntityNames
import com.nagopy.android.aplin.view.preference.MultiSelectionItem
import com.nagopy.android.kotlinames.equalTo
import com.nagopy.android.kotlinames.greaterThan
import io.realm.RealmQuery

public enum class Category(val minSdkVersion: Int, val maxSdkVersion: Int, val titleResourceId: Int, val summaryResourceId: Int) : MultiSelectionItem {

    ALL(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_all, R.string.category_all_summary),
    SYSTEM(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system, R.string.category_system_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
        }
    },
    SYSTEM_UNDISABLABLE(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system_undisablable, R.string.category_system_undisablable_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
                    .beginGroup()
                    .equalTo(AppEntityNames.isThisASystemPackage(), true)
                    .or().equalTo(AppEntityNames.hasActiveAdmins(), true)
                    .endGroup()
        }
    },
    SYSTEM_DISABLABLE(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system_disablable, R.string.category_system_disablable_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
                    .not().beginGroup()
                    .equalTo(AppEntityNames.isThisASystemPackage(), true)
                    .or().equalTo(AppEntityNames.hasActiveAdmins(), true)
                    .endGroup()
        }
    },
    DISABLED(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_disabled, R.string.category_disabled_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isEnabled(), false)
        }
    },
    DEFAULT(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_default, R.string.category_default_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isDefaultApp(), true)
        }
    },
    USER(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_user, R.string.category_user_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), false)
        }
    },
    RECENTLY_USED(Build.VERSION_CODES.LOLLIPOP, Int.MAX_VALUE, R.string.category_recently_used, R.string.category_recently_used_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.greaterThan(AppEntityNames.launchTimes(), 0)
        }
    }
    ;

    open fun isTarget(appData: AppEntity): Boolean = true
    open fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> = realmQuery

    override fun getTitle(context: Context): String {
        return context.getString(titleResourceId)
    }

    override fun getSummary(context: Context): String {
        return context.getString(summaryResourceId)
    }

    override fun minSdkVersion(): Int = minSdkVersion

    override fun maxSdkVersion(): Int = maxSdkVersion

}
