package com.nagopy.android.aplin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class Navigator(val activity: Activity) {

    fun startApplicationDetailSettings(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        activity.startActivity(intent)
    }

}
