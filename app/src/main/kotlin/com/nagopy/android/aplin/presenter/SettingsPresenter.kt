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

package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import com.nagopy.android.aplin.view.MainActivity
import com.nagopy.android.aplin.view.SettingsView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SettingsPresenter @Inject constructor() : Presenter, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    var settingChanged: Boolean = false

    lateinit var view: SettingsView

    fun initialize(view: SettingsView) {
        this.view = view
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
