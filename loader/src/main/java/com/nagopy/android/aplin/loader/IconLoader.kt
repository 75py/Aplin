package com.nagopy.android.aplin.loader

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import timber.log.Timber

class IconLoader(val packageManager: PackageManager
                 , val defaultIcon: Drawable) {

    val iconCache = HashMap<String, Drawable>()

    fun loadIcon(packageName: String): Drawable {
        var icon = iconCache[packageName]
        if (icon == null) {
            icon = try {
                packageManager.getApplicationIcon(packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.v(e, "Error pkg=%s", packageName)
                defaultIcon
            }
            iconCache[packageName] = icon
            Timber.v("Add cache. pkg=s%s", packageName)
        } else {
            Timber.v("Cached. pkg=%s", packageName)
        }
        return icon!!
    }

}
