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
import android.provider.Settings
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ProgressBar
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.AdapterViewProtocol
import androidx.test.espresso.action.AdapterViewProtocols
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.nagopy.android.aplin.BuildConfig
import com.nagopy.android.aplin.NoAnimationTestRule
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.TestFunction.intentBlock
import com.nagopy.android.aplin.TestResources
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
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
import kotlin.test.fail
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    @Rule
    @JvmField
    val noAnimationTestRule = NoAnimationTestRule()

    private val timeout = 500L
    private val testPackageName = BuildConfig.APPLICATION_ID + ".test"

    lateinit var uiDevice: UiDevice
    lateinit var sp: SharedPreferences

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        sp = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getInstrumentation().targetContext)
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
                .perform(ViewActions.replaceText("nagopy"))
                .perform(ViewActions.pressImeActionButton())

        clickAll { app ->
            assertTrue(app.packageName.contains("nagopy"), app.toString())
        }
    }


    @LargeTest
    @Test
    fun all() {
        start()
        switchCategory(Category.ALL)
        clickAll { app ->
            if (app.packageName != testPackageName) {
                // アプリ名が表示されていることを確認
                assertTrue(uiDevice.findObject(UiSelector().text(app.label)).waitForExists(timeout), app.toString())
            }
        }
    }

    @LargeTest
    @Test
    fun SYSTEM_DISABLABLE() {
        start()
        switchCategory(Category.SYSTEM_DISABLABLE)
        val errors = ArrayList<Throwable>()
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
            } catch (t: Throwable) {
                Timber.e(t, "Continue")
                errors.add(t)
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
            if (uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).waitForExists(timeout)) {
                assertFalse(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).isEnabled, app.toString())
            }
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

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    // Android 10未満のみ
                    // Android 10では権限グループを知る手段がない
                    // https://developer.android.com/about/versions/10/privacy/changes#permission-groups-removed
                    app.permissionGroups.forEach {
                        val exists = uiDevice.findObject(UiSelector().text(it.label)).waitForExists(timeout)
                        if (!exists) {
                            Timber.w("pkg=%s cannot deny permission %s", app.packageName, it)
                            errors.add(app.packageName)
                        }
                    }
                }

                uiDevice.pressBack()
            } else {
                // 「権限」がクリックできない
                Timber.w("pkg=%s cannot deny any permissions.", app.packageName)
                errors.add(app.packageName)
            }
        }

        assertTrue(errors.size < 10, "Too many errors. $errors")
        assertTrue(errors.filter { !it.startsWith("com.android") }.count() < 3, "Too many errors. $errors")
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
        val errors = ArrayList<App>()
        var i = 0
        do {
            try {
                var skipFlag = false

                intentBlock {
                    onData(instanceOf(App::class.java))
                            .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                            .atPosition(i)
                            .usingAdapterViewProtocol(appListViewProtocol)
                            .perform(click())
                    uiDevice.waitForIdle()
                    val packageName = appListViewProtocol.apps[i].packageName
                    Timber.i("packageName=%s", packageName)

                    if (setOf("com.google.android.angle",
                                    "com.google.android.captiveportallogin",
                                    "com.google.android.modulemetadata",
                                    "com.google.android.networkstack",
                                    "com.google.android.networkstack.permissionconfig",
                                    "com.google.android.documentsui",
                                    "com.google.android.ims"
                            ).contains(packageName)) {
                        skipFlag = true
                        return@intentBlock
                    }

                    Intents.intended(allOf(
                            IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS), IntentMatchers.hasData("package:$packageName")
                    ))
                }

                if (skipFlag) {
                    i += (appListViewProtocol.apps.count() / 20 + 1)
                    continue
                }

                // validation
                validator(appListViewProtocol.apps[i])

                // Back to Aplin
                uiDevice.pressBack()
                uiDevice.waitForIdle()
            } catch (e: Error) {
                errors.add(appListViewProtocol.apps[i])
            }
            i += (appListViewProtocol.apps.count() / 10 + 1)
        } while (i < appListViewProtocol.apps.count())

        if (errors.isNotEmpty()) {
            fail(errors.toString())
        }
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
