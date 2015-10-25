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
import com.nagopy.android.easyprefs.MultiSelectionItem

public enum class Category(val minSdkVersion: Int, val maxSdkVersion: Int, val titleResourceId: Int, val summaryResourceId: Int) : MultiSelectionItem {

    ALL(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_all, R.string.category_all_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return true
        }
    },
    SYSTEM(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system, R.string.category_system_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return appData.isSystem
        }
    },
    SYSTEM_UNDISABLABLE(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system_undisablable, R.string.category_system_undisablable_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM.isTarget(appData) && (appData.isThisASystemPackage || appData.hasActiveAdmins)
        }
    },
    SYSTEM_DISABLABLE(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_system_disablable, R.string.category_system_disablable_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return SYSTEM.isTarget(appData) && !SYSTEM_UNDISABLABLE.isTarget(appData)
        }
    },
    DISABLED(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_disabled, R.string.category_disabled_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return !appData.isEnabled
        }
    },
    DEFAULT(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_default, R.string.category_default_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return appData.isDefaultApp
        }
    },
    USER(Build.VERSION_CODES.BASE, Int.MAX_VALUE, R.string.category_user, R.string.category_user_summary) {
        override fun isTarget(appData: AppEntity): Boolean {
            return !appData.isSystem
        }
    },
    RECENTLY_USED(Build.VERSION_CODES.LOLLIPOP, Int.MAX_VALUE, R.string.category_recently_used, R.string.category_recently_used_summary) {
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
