package com.nagopy.android.aplin.view.preference

import android.content.Context
import android.preference.CheckBoxPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.util.AttributeSet
import com.nagopy.android.aplin.R

import com.nagopy.android.aplin.model.Category

class CategoryPreference : PreferenceCategory {

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

        setTitle(R.string.category)
        Category.values.forEach {
            val preference = CheckBoxPreference(context)
            preference.title = it.getTitle(context)
            preference.summary = it.getSummary(context)
            preference.key = it.javaClass.name + "_" + it.name
            addPreference(preference)
        }
    }
}
