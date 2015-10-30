/*
 * Copyright (C) 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagopy.android.aplin.view

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.presenter.SettingsPresenter
import javax.inject.Inject


public class SettingsActivity : AppCompatActivity(), SettingsView, Preference.OnPreferenceClickListener {

    @Inject
    lateinit var settingsPresenter: SettingsPresenter

    lateinit var settingsFragment: PreferenceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Aplin.getApplicationComponent().inject(this)
        settingsPresenter.initialize(this)

        setContentView(R.layout.activity_settings)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        settingsFragment = SettingsFragment()
        fragmentManager.beginTransaction().replace(R.id.content, settingsFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        settingsPresenter.resume()
        findUsageStatsPreference().onPreferenceClickListener = this
    }

    override fun onPause() {
        super.onPause()
        settingsPresenter.pause()
        findUsageStatsPreference().onPreferenceClickListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsPresenter.destroy()
    }

    override fun finish() {
        if (!settingsPresenter.finish()) {
            super.finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> throw RuntimeException("unknown id:" + item.itemId)
        }
        return super.onOptionsItemSelected(item)
    }

    public class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref)
        }
    }

    fun findUsageStatsPreference() = settingsFragment.findPreference(getText(R.string.usage_stats_key))

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            getText(R.string.usage_stats_key) -> {
                settingsPresenter.onUsageStatsPreferenceClicked()
            }
        }
        return true
    }

    override fun setUsageStatsTitle(resId: Int) {
        findUsageStatsPreference().setTitle(resId)
    }

    override fun setUsageStatsSummary(resId: Int) {
        findUsageStatsPreference().setSummary(resId)
    }

}
