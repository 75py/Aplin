package com.nagopy.android.aplin.model

import android.app.Application
import android.content.SharedPreferences
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton open class Analytics
@Inject constructor(
        val application: Application,
        val sharedPreferences: SharedPreferences
) {

    val googleAnalytics: GoogleAnalytics = GoogleAnalytics.getInstance(application)
    val tracker: Tracker
    val key = application.getString(R.string.ga_preference_key)

    init {
        googleAnalytics.appOptOut = !isAgreed()
        googleAnalytics.setDryRun(BuildConfig.DEBUG)
        tracker = googleAnalytics.newTracker(application.getString(R.string.ga_trackingId))
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    open fun isConfirmed(): Boolean = sharedPreferences.contains(key)

    open fun isAgreed(): Boolean = sharedPreferences.getBoolean(key, false)

    open fun agree() = sharedPreferences.edit().putBoolean(key, true).commit()
    open fun disagree() = sharedPreferences.edit().putBoolean(key, false).commit()

    open fun click(pkg: String) {
        if (isAgreed()) {
            tracker.send(
                    HitBuilders.EventBuilder()
                            .setCategory("UI")
                            .setAction("click_pkg")
                            .setLabel(pkg)
                            .build()
            )
        }
    }

    open fun longClick(pkg: String) {
        if (isAgreed()) {
            tracker.send(
                    HitBuilders.EventBuilder()
                            .setCategory("UI")
                            .setAction("long_click_pkg")
                            .setLabel(pkg)
                            .build()
            )
        }
    }

    open fun menuClick(menuTitle: String) {
        if (isAgreed()) {
            tracker.send(
                    HitBuilders.EventBuilder()
                            .setCategory("UI")
                            .setAction("menu_click")
                            .setLabel(menuTitle)
                            .build()
            )
        }
    }

    open fun settingChanged(key: String, newValue: Any?) {
        if (isAgreed()) {
            tracker.send(
                    HitBuilders.EventBuilder()
                            .setCategory("Settings")
                            .setAction("setting_changed")
                            .setLabel("$key=$newValue")
                            .build()
            )
        }
    }

}
