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

import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class UsageStatsHelper
@Inject constructor(val application: Application
                    , val appOpsManager: AppOpsManager) {

    open fun isUsageStatsAllowed(): Boolean {
        val uid = android.os.Process.myUid();
        val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid,
                application.packageName);
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    open fun startSettingActivity(context: Context) {
        val intent = Intent("android.settings.USAGE_ACCESS_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        context.startActivity(intent)
    }

}