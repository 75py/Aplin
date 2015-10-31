package com.nagopy.android.aplin.model

import android.app.Application
import android.content.SharedPreferences
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
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
        tracker = googleAnalytics.newTracker(application.getString(R.string.ga_trackingId))
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    open fun isConfirmed(): Boolean = sharedPreferences.contains(key)

    open fun isAgreed(): Boolean = sharedPreferences.getBoolean(key, false)

    open fun agree() = sharedPreferences.edit().putBoolean(key, true).commit()
    open fun disagree() = sharedPreferences.edit().putBoolean(key, false).commit()

    open fun show(screen: String) {
        tracker.send(
                HitBuilders.EventBuilder()
                        .setCategory("Aplin")
                        .setAction("show")
                        .setLabel(screen)
                        .build()
        )
    }

    open fun click(pkg: String) {
        tracker.send(
                HitBuilders.EventBuilder()
                        .setCategory("Aplin")
                        .setAction("click_pkg")
                        .setLabel(pkg)
                        .build()
        )
    }

}
