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

import android.support.test.filters.SmallTest
import com.nagopy.android.aplin.entity.App
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CategoryTest {

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @SmallTest
    @Test
    fun overlay_systemApp() {
        val list = listOf(App().apply {
            // false
            isSystemPackage = true
        }, App().apply {
            // false
            isSystemPackage = true
            requestedPermissions.add(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        }, App().apply {
            // false
            isSystemPackage = false
        }, App().apply {
            // false
            isSystemPackage = false
            requestedPermissions.add(android.Manifest.permission.INTERNET)
        }, App().apply {
            // true
            isSystemPackage = false
            requestedPermissions.add(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        })
        val filtered = Category.SYSTEM_ALERT_WINDOW_PERMISSION.where(list)
        assertEquals(1, filtered.count())
    }

}