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

package com.nagopy.android.aplin.view.preference

import android.content.Context
import android.os.Build
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.util.AttributeSet
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Sort

class SortPreference : PreferenceCategory, Preference.OnPreferenceChangeListener {

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressWarnings("unused")
    constructor(context: Context) : super(context)

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)

        setTitle(R.string.sort)
        key = Sort::class.java.name
        Sort.values()
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach {
                    val preference = CheckBoxPreference(context)
                    preference.widgetLayoutResource = R.layout.preference_widget_checkbox_single
                    preference.setTitle(it.titleResourceId)
                    preference.setSummary(it.summaryResourceId)
                    preference.key = it.key
                    preference.setDefaultValue(it.defaultValue)
                    addPreference(preference)
                    preference.onPreferenceChangeListener = this
                }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (newValue == false) {
            return false
        }

        Sort.values()
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach {
                    val p = findPreference(it.key) as CheckBoxPreference
                    val isChecked = p === preference
                    p.isChecked = isChecked
                    if (isChecked) {
                        sharedPreferences.edit().putString(key, it.name).apply()
                    }
                }
        return false
    }

}
