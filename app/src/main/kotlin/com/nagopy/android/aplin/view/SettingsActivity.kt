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
package com.nagopy.android.aplin.view

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.presenter.SettingsPresenter
import javax.inject.Inject


class SettingsActivity : AppCompatActivity(), SettingsView {

    @Inject
    lateinit var settingsPresenter: SettingsPresenter

    lateinit var settingsFragment: PreferenceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Aplin.getApplicationComponent().inject(this)
        settingsPresenter.initialize(this)

        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        settingsFragment = SettingsFragment()
        fragmentManager.beginTransaction().replace(R.id.content, settingsFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        settingsPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        settingsPresenter.pause()
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

    companion object {
        class SettingsFragment : PreferenceFragment() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                addPreferencesFromResource(R.xml.pref)
            }
        }
    }
}
