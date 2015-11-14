package com.nagopy.android.aplin.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.model.Applications
import timber.log.Timber
import javax.inject.Inject

class PackageChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var applications: Applications

    override fun onReceive(context: Context, intent: Intent) {
        Aplin.getApplicationComponent().inject(this)

        val uid = intent.getIntExtra(Intent.EXTRA_UID, Integer.MIN_VALUE)
        //        val pkg = context.packageManager.getNameForUid(uid);
        val pkg = intent.data.schemeSpecificPart

        Timber.i("action=${intent.action}, uid=$uid, pkg=$pkg, ${intent.extras}")
        if (pkg != null) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> applications.insert(pkg)
                Intent.ACTION_PACKAGE_CHANGED -> applications.update(pkg)
                Intent.ACTION_PACKAGE_REPLACED -> applications.update(pkg)
                Intent.ACTION_PACKAGE_REMOVED -> applications.delete(pkg)
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> applications.delete(pkg)
            }
        }
    }
}