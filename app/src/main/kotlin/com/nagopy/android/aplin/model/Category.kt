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
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.VersionCode
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.easyprefs.MultiSelectionItem

public enum class Category(val minSdkVersion: Int, val maxSdkVersion: Int, val titleResourceId: Int, val summaryResourceId: Int) : MultiSelectionItem {

    ALL(VersionCode.BASE, Int.MAX_VALUE, R.string.category_all, R.string.category_all_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return true
        }
    },
    RUNNING(VersionCode.BASE, VersionCode.KITKAT, R.string.category_all_running, R.string.category_all_running_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return !appData.process.isEmpty()
        }
    },
    SYSTEM(VersionCode.BASE, Int.MAX_VALUE, R.string.category_system, R.string.category_system_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return appData.isSystem
        }
    },
    SYSTEM_RUNNING(VersionCode.BASE, VersionCode.KITKAT, R.string.category_system_running, R.string.category_system_running_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM.isTarget(appData) && RUNNING.isTarget(appData)
        }
    },
    SYSTEM_UNDISABLABLE(VersionCode.BASE, Int.MAX_VALUE, R.string.category_system_undisablable, R.string.category_system_undisablable_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM.isTarget(appData) && (appData.isThisASystemPackage || appData.hasActiveAdmins)
        }
    },
    SYSTEM_UNDISABLABLE_RUNNING(VersionCode.BASE, VersionCode.KITKAT, R.string.category_system_undisablable_running, R.string.category_system_undisablable_running_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM_UNDISABLABLE.isTarget(appData) && RUNNING.isTarget(appData)
        }
    },
    SYSTEM_DISABLABLE(VersionCode.BASE, Int.MAX_VALUE, R.string.category_system_disablable, R.string.category_system_disablable_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM.isTarget(appData) && !SYSTEM_UNDISABLABLE.isTarget(appData)
        }
    },
    SYSTEM_DISABLABLE_RUNNING(VersionCode.BASE, VersionCode.KITKAT, R.string.category_system_disablable_running, R.string.category_system_disablable_running_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM_DISABLABLE.isTarget(appData) && RUNNING.isTarget(appData)
        }
    },
    DISABLED(VersionCode.BASE, Int.MAX_VALUE, R.string.category_disabled, R.string.category_disabled_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return !appData.isEnabled
        }
    },
    DEFAULT(VersionCode.BASE, Int.MAX_VALUE, R.string.category_default, R.string.category_default_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return appData.isDefaultApp
        }
    },
    USER(VersionCode.BASE, Int.MAX_VALUE, R.string.category_user, R.string.category_user_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return !appData.isSystem
        }
    },
    USER_RUNNING(VersionCode.BASE, VersionCode.KITKAT, R.string.category_user_running, R.string.category_user_running_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return USER.isTarget(appData) && RUNNING.isTarget(appData)
        }
    },
    RECENTLY_USED(VersionCode.LOLLIPOP, Int.MAX_VALUE, R.string.category_recently_used, R.string.category_recently_used_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return appData.launchTimes > 0
        }
    }
    ;

    public abstract fun isTarget(appData: AppEntity): Boolean

    override fun getTitle(context: Context): String {
        return context.getString(titleResourceId)
    }

    override fun getSummary(context: Context): String {
        return context.getString(summaryResourceId)
    }

    override fun minSdkVersion(): Int = minSdkVersion

    override fun maxSdkVersion(): Int = maxSdkVersion

}
