package com.nagopy.android.aplin.view.preference

import android.content.Context
import android.os.Build
import android.preference.CheckBoxPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.util.AttributeSet
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.DisplayItem

class DisplayItemPreference : PreferenceCategory {

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

        setTitle(R.string.display_item)
        DisplayItem.values
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach {
                    val preference = CheckBoxPreference(context)
                    preference.setTitle(it.titleResourceId)
                    preference.setSummary(it.summaryResourceId)
                    preference.key = it.key
                    preference.setDefaultValue(it.defaultValue)
                    addPreference(preference)
                }
    }
}
