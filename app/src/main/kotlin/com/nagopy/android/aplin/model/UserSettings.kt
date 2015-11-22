package com.nagopy.android.aplin.model

import android.content.SharedPreferences
import android.os.Build
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Singleton class UserSettings
@Inject constructor(var sharedPreferences: SharedPreferences) {

    val sort: Sort by sortProperty()

    val displayItems: List<DisplayItem> by displayItemProperty()

    val categories: List<Category> by categoryProperty()

    class categoryProperty : ReadOnlyProperty<UserSettings, List<Category>> {
        override fun getValue(thisRef: UserSettings, property: KProperty<*>): List<Category> {
            val selectedValues = ArrayList<Category>()
            Category.values.forEach {
                val checked = thisRef.sharedPreferences.getBoolean(it.key, it.defaultValue)
                if (checked) {
                    selectedValues.add(it)
                }
            }
            return if (selectedValues.isEmpty()) {
                Category.values
                        .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                        .filter { it.defaultValue }
            } else {
                selectedValues
            }
        }
    }

    class displayItemProperty : ReadOnlyProperty<UserSettings, List<DisplayItem>> {
        override fun getValue(thisRef: UserSettings, property: KProperty<*>): List<DisplayItem> {
            val v = ArrayList<DisplayItem>()
            DisplayItem.values.forEach {
                val checked = thisRef.sharedPreferences.getBoolean(it.key, it.defaultValue)
                if (checked) {
                    v.add(it)
                }
            }
            return v
        }

    }

    class sortProperty : ReadOnlyProperty<UserSettings, Sort> {
        override fun getValue(thisRef: UserSettings, property: KProperty<*>): Sort {
            val value = thisRef.sharedPreferences.getString(Sort::class.java.name, "")
            return if (value.isEmpty()) {
                Sort.DEFAULT
            } else {
                Sort.valueOf(value)
            }
        }
    }
}