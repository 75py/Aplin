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

import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.entity.names.AppEntityNames
import com.nagopy.android.kotlinames.equalTo
import com.nagopy.android.kotlinames.greaterThan
import io.realm.RealmQuery

public enum class Category(
        val titleResourceId: Int
        , val summaryResourceId: Int
        , val targetSdkVersion: IntRange = Constants.ALL_SDK_VERSION
        , val defaultValue: Boolean = false) {

    ALL(titleResourceId = R.string.category_all
            , summaryResourceId = R.string.category_all_summary
            , defaultValue = true)

    ,
    SYSTEM(
            titleResourceId = R.string.category_system
            , summaryResourceId = R.string.category_system_summary
            , defaultValue = true) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
        }
    }
    ,
    SYSTEM_UNDISABLABLE(
            titleResourceId = R.string.category_system_undisablable
            , summaryResourceId = R.string.category_system_undisablable_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
                    .beginGroup()
                    .equalTo(AppEntityNames.isThisASystemPackage(), true)
                    .or().equalTo(AppEntityNames.hasActiveAdmins(), true)
                    .endGroup()
        }
    }
    ,
    SYSTEM_DISABLABLE(titleResourceId = R.string.category_system_disablable
            , summaryResourceId = R.string.category_system_disablable_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), true)
                    .not().beginGroup()
                    .equalTo(AppEntityNames.isThisASystemPackage(), true)
                    .or().equalTo(AppEntityNames.hasActiveAdmins(), true)
                    .endGroup()
        }
    }
    ,
    DISABLED(titleResourceId = R.string.category_disabled
            , summaryResourceId = R.string.category_disabled_summary
            , defaultValue = true) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isEnabled(), false)
        }
    }
    ,
    DEFAULT(titleResourceId = R.string.category_default
            , summaryResourceId = R.string.category_default_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isDefaultApp(), true)
        }
    }
    ,
    USER(titleResourceId = R.string.category_user
            , summaryResourceId = R.string.category_user_summary
            , defaultValue = true) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.equalTo(AppEntityNames.isSystem(), false)
        }
    }
    ,
    RECENTLY_USED(titleResourceId = R.string.category_recently_used
            , summaryResourceId = R.string.category_recently_used_summary) {
        override fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> {
            return realmQuery.greaterThan(AppEntityNames.launchTimes(), 0)
        }
    }
    ;

    open fun where(realmQuery: RealmQuery<AppEntity>): RealmQuery<AppEntity> = realmQuery

    val key: String = "${javaClass.name}_$name"
}
