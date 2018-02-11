package com.nagopy.android.aplin.loader.internal

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import timber.log.Timber
import java.lang.reflect.Method

internal class AplinPackageManager(val packageManager: PackageManager) {

    private val getHomeActivitiesMethod: Method? by lazy {
        try {
            val m = PackageManager::class.java.getDeclaredMethod("getHomeActivities", List::class.java)
            m.isAccessible = true
            return@lazy m
        } catch (e: Exception) {
            Timber.e(e)
            return@lazy null
        }
    }

    // https://github.com/aosp-mirror/platform_packages_apps_settings/blob/android-cts-8.1_r1/src/com/android/settings/applications/InstalledAppDetails.java
    private fun signaturesMatch(pkg1: String?, pkg2: String?): Boolean {
        if (pkg1 != null && pkg2 != null) {
            try {
                val match = packageManager.checkSignatures(pkg1, pkg2)
                if (match >= PackageManager.SIGNATURE_MATCH) {
                    return true
                }
            } catch (e: Exception) {
                // e.g. named alternate package not found during lookup;
                // this is an expected case sometimes
                Timber.e(e)
            }

        }
        return false
    }

    fun getHomePackages(): Collection<String> {
        if (getHomeActivitiesMethod != null) {
            val homeActivities = ArrayList<ResolveInfo>()
            getHomeActivitiesMethod!!.invoke(packageManager, homeActivities)
            val homePackages = HashSet<String>()
            homeActivities.forEach {
                val activityPkg = it.activityInfo.packageName
                homePackages.add(activityPkg)
                it.activityInfo.metaData?.let {
                    val metaPkg = it.getString(ActivityManager.META_HOME_ALTERNATE);
                    if (signaturesMatch(metaPkg, activityPkg)) {
                        homePackages.add(metaPkg);
                    }
                }
            }
            return homePackages
        } else {
            // 諦めてそれらしく取る
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            return packageManager.queryIntentActivities(intent, 0)
                    .map { it.activityInfo.packageName }
                    .plus("com.google.android.launcher") // 仕組みが未確認だが、これはホームアプリ判定になっているっぽい
        }

    }

}
