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

import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.AdapterViewProtocol
import android.support.test.espresso.action.AdapterViewProtocols
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ProgressBar
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.TestFunction.intentBlock
import com.nagopy.android.aplin.TestResources
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    val timeout = 500L

    lateinit var uiDevice: UiDevice
    lateinit var sp: SharedPreferences

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        sp = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
    }

    @After
    fun tearDown() {
        uiDevice.pressBack()
        rule.activity?.finish()
    }

    private fun startActivity(): MainActivity = rule.launchActivity(null)

    @LargeTest
    @Test
    fun testLaunch() {
        startActivity()

        val activity = rule.activity
        assertNotNull(activity.presenter)
        assertNotNull(activity.adPresenter)
        assertNotNull(activity.adView)
        assertNotNull(activity.toolbar)
        assertNotNull(activity.viewPager)
        assertNotNull(activity.progressBar)
    }


    @LargeTest
    @Test
    fun textFilter() {
        start()
        switchCategory(Category.ALL)

        onView(withId(R.id.action_search))
                .perform(click())

        onView(withId(R.id.search_src_text))
                .perform(ViewActions.typeText("nagopy"))

        clickAll { app ->
            assertTrue(app.packageName.contains("nagopy"), app.toString())
        }

        onView(withId(R.id.search_src_text))
                .perform(ViewActions.clearText())
    }


    @LargeTest
    @Test
    fun all() {
        start()
        switchCategory(Category.ALL)
        clickAll { app ->
            // アプリ名が表示されていることを確認
            assertTrue(uiDevice.findObject(UiSelector().text(app.label)).waitForExists(timeout), app.toString())
        }
    }

    @LargeTest
    @Test
    fun SYSTEM_DISABLABLE() {
        start()
        switchCategory(Category.SYSTEM_DISABLABLE)
        val errors = ArrayList<AssertionError>()
        clickAll { app ->
            try {
                // アプリ名が表示されていることを確認
                assertTrue(uiDevice.findObject(UiSelector().text(app.label)).waitForExists(timeout), app.toString())

                val disableButtonLabel =
                        if (app.isEnabled && !app.isDisabledUntilUsed) {
                            TestResources.string.test_btn_disable
                        } else {
                            TestResources.string.test_btn_enable
                        }
                assertTrue(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).waitForExists(timeout), app.toString())
                assertTrue(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).isEnabled, app.toString())
            } catch (e: AssertionError) {
                Timber.e(e, "Continue")
                errors.add(e)
            }
        }

        errors.forEach {
            Timber.e(it)
        }
        if (errors.isNotEmpty()) {
            throw errors[0]
        }
    }

    @LargeTest
    @Test
    fun SYSTEM_UNDISABLABLE() {
        start()
        switchCategory(Category.SYSTEM_UNDISABLABLE)
        clickAll { app ->
            // アプリ名が表示されていることを確認
            assertTrue(uiDevice.findObject(UiSelector().text(app.label)).waitForExists(timeout), app.toString())

            val disableButtonLabel =
                    if (app.isEnabled && !app.isDisabledUntilUsed) {
                        TestResources.string.test_btn_disable
                    } else {
                        TestResources.string.test_btn_enable
                    }
            assertTrue(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).waitForExists(timeout), app.toString())
            assertFalse(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).isEnabled, app.toString())
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @LargeTest
    @Test
    fun DENIABLE_PERMISSIONS() {
        val errors = HashSet<String>()

        start()
        switchCategory(Category.DENIABLE_PERMISSIONS)
        clickAll { app ->
            // アプリ名が表示されていることを確認
            assertTrue(uiDevice.findObject(UiSelector().text(app.label)).waitForExists(timeout), app.toString())

            // 「権限」が表示されている
            assertTrue(uiDevice.findObject(UiSelector().text(TestResources.string.test_permissions)).waitForExists(timeout), app.toString())

            if (uiDevice.findObject(UiSelector().text(TestResources.string.test_permissions)).isEnabled) {
                // 「権限」がクリックできる
                //  クリックして次の画面へ
                uiDevice.findObject(UiSelector().text(TestResources.string.test_permissions)).clickAndWaitForNewWindow(timeout)

                app.permissionGroups.forEach {
                    val exists = uiDevice.findObject(UiSelector().text(it.label)).waitForExists(timeout)
                    if (!exists) {
                        Timber.d("pkg=%s cannot deny permission %s", app.packageName, it)
                        errors.add(app.packageName)
                    }
                }

                uiDevice.pressBack()
            } else {
                // 「権限」がクリックできない
                Timber.d("pkg=%s cannot deny any permissions.", app.packageName)
                errors.add(app.packageName)
            }
        }

        assertTrue(errors.size < 10, "Too many errors. " + errors.toString())
        assertTrue(errors.filter { !it.startsWith("com.android") }.count() < 3, "Too many errors. " + errors.toString())
    }

    private fun start() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)
        uiDevice.waitForIdle()

        Thread.sleep(5000) // Wait for loading
    }

    private fun switchCategory(category: Category) {
        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(category)))
                .atPosition(0)
                .perform(click())
        uiDevice.waitForIdle()
    }

    private fun clickAll(validator: (app: App) -> Unit) {
        val appListViewProtocol = AppListViewProtocol()
        var i = 0
        do {
            intentBlock {
                onData(instanceOf(App::class.java))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .atPosition(i)
                        .usingAdapterViewProtocol(appListViewProtocol)
                        .perform(click())
                uiDevice.waitForIdle()
                val packageName = appListViewProtocol.apps[i].packageName
                Timber.d("packageName=%s", packageName)
                Intents.intended(allOf(
                        IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        , IntentMatchers.hasData("package:$packageName")
                ))
            }

            // validation
            validator(appListViewProtocol.apps[i])

            // Back to Aplin
            uiDevice.pressBack()
            uiDevice.waitForIdle()
            i++
        } while (i < appListViewProtocol.apps.count())
    }

    class AppListViewProtocol : AdapterViewProtocol by AdapterViewProtocols.standardProtocol() {

        var apps: List<App> = emptyList()

        override fun getDataInAdapterView(adapterView: AdapterView<out Adapter>?): MutableIterable<AdapterViewProtocol.AdaptedData> {
            val result = AdapterViewProtocols.standardProtocol().getDataInAdapterView(adapterView)
            apps = result.map { it.getData() as App }.toList()
            return result
        }
    }

}
