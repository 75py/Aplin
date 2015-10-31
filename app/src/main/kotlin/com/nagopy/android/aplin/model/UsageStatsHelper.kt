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