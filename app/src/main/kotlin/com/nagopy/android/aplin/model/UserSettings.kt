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