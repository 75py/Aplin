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

import android.app.Application
import com.nagopy.android.aplin.entity.App
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class DisplayItemTest {

    lateinit var context: Application

    var app = App()

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun packageName() {
        val sb = StringBuilder()
        app.packageName = "com.nagopy.android.test"
        DisplayItem.PACKAGE_NAME.append(context, sb, app)

        assertEquals(app.packageName, sb.toString())
    }

}