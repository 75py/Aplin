package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Analytics
import com.nagopy.android.aplin.model.Apps
import com.nagopy.android.aplin.model.UsageStatsHelper
import com.nagopy.android.aplin.view.MainActivity
import com.nagopy.android.aplin.view.SettingsView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SettingsPresenter : Presenter, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var apps: Apps

    @Inject
    lateinit var usageStatsHelper: UsageStatsHelper

    @Inject
    lateinit var analytics: Analytics

    var settingChanged: Boolean = false

    lateinit var view: SettingsView

    @Inject
    constructor() {
    }

    fun initialize(view: SettingsView) {
        this.view = view
        settingChanged = false
    }

    override fun resume() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        view.setUsageStatsTitle(if (usageStatsHelper.isUsageStatsAllowed()) {
            R.string.usage_stats_title_enabled
        } else {
            R.string.usage_stats_title_disabled
        })
        view.setUsageStatsSummary(if (usageStatsHelper.isUsageStatsAllowed()) {
            R.string.usage_stats_summary_enabled
        } else {
            R.string.usage_stats_summary_disabled
        })
    }

    override fun pause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun destroy() {
    }

    /**
     * Activity#finish
     * @return True if the presenter has consumed the event, false otherwise.
     */
    fun finish(): Boolean {
        if (settingChanged) {
            apps.invalidateCache()
            val mainActivityIntent = Intent(application, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(mainActivityIntent)
            return true
        }
        return false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        settingChanged = true
        analytics.settingChanged(key!!, sharedPreferences!!.all[key])
    }

    open fun onUsageStatsPreferenceClicked() {
        usageStatsHelper.startSettingActivity(application)
    }
}