package com.nagopy.android.aplin.view.preference

import android.content.Context
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.util.AttributeSet
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Sort

class SortPreference : PreferenceCategory, Preference.OnPreferenceChangeListener {

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    @SuppressWarnings("unused")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    @SuppressWarnings("unused")
    constructor(context: Context) : super(context) {
    }

    override fun onAttachedToHierarchy(preferenceManager: PreferenceManager) {
        super.onAttachedToHierarchy(preferenceManager)

        setTitle(R.string.sort)
        key = Sort::class.java.name
        Sort.values.forEach {
            val preference = CheckBoxPreference(context)
            preference.widgetLayoutResource = R.layout.preference_widget_checkbox_single
            preference.title = it.getTitle(context)
            preference.summary = it.getSummary(context)
            preference.key = it.javaClass.name + "_" + it.name
            addPreference(preference)
            preference.onPreferenceChangeListener = this
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        if (newValue == false) {
            return false
        }

        Sort.values.forEach {
            val p = findPreference(it.javaClass.name + "_" + it.name) as CheckBoxPreference
            val isChecked = p === preference
            p.isChecked = isChecked
            if (isChecked) {
                sharedPreferences.edit().putString(key, it.name).apply()
            }
        }
        return false
    }

}
