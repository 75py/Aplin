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

package com.nagopy.android.aplin.presenter

import android.app.Activity
import android.app.Application
import android.os.Build
import android.provider.Settings
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.filters.MediumTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.ApplicationMockComponent
import com.nagopy.android.aplin.ApplicationMockModule
import com.nagopy.android.aplin.DaggerApplicationMockComponent
import com.nagopy.android.aplin.TestFunction.intentBlock
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.view.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

class MainScreenPresenterTest {

    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @Inject
    lateinit var mainScreenPresenter: MainScreenPresenter

    var activity: MainActivity? = null

    lateinit var uiDevice: UiDevice

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        Aplin.component = DaggerApplicationMockComponent.builder()
                .applicationMockModule(ApplicationMockModule(application))
                .build()

        (Aplin.getApplicationComponent() as ApplicationMockComponent).inject(this)
    }

    @After
    fun tearDown() {
        uiDevice.pressBack()
        uiDevice.pressBack()
        uiDevice.waitForIdle(1000)
    }

    @MediumTest
    @Test
    fun listItemClicked_ALL() {
        activity = rule.launchActivity(null)

        intentBlock {
            val app = App()
            app.packageName = "com.nagopy.android.aplin"
            mainScreenPresenter.listItemClicked(activity as Activity, app, Category.ALL)

            Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
            Intents.intended(IntentMatchers.hasData("package:com.nagopy.android.aplin"))
        }
    }

    @MediumTest
    @Test
    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    fun listItemClicked_OVERLAY() {
        val activity = rule.launchActivity(null)

        intentBlock {
            val app = App()
            app.packageName = "com.nagopy.android.aplin"
            mainScreenPresenter.listItemClicked(activity, app, Category.SYSTEM_ALERT_WINDOW_PERMISSION)

            Intents.intended(IntentMatchers.hasAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            Intents.intended(IntentMatchers.hasData("package:com.nagopy.android.aplin"))
        }
    }

}