package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.nagopy.android.aplin.model.Apps
import com.nagopy.android.aplin.view.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPresenter : Presenter, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var apps: Apps

    var settingChanged: Boolean = false

    @Inject
    constructor() {
    }

    fun initialize() {
        settingChanged = false
    }

    override fun resume() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
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
    }
}