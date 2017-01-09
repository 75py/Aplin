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
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeDown
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.test.suitebuilder.annotation.LargeTest
import android.test.suitebuilder.annotation.MediumTest
import android.widget.ProgressBar
import com.nagopy.android.aplin.AplinTestRule
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.TestFunction.intentBlock
import com.nagopy.android.aplin.TestResources
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import io.realm.Realm
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasToString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    @Rule
    @JvmField
    val aplinRule = AplinTestRule()

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

    @MediumTest
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
    fun all() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.ALL)))
                .perform(click())

        waitForIdle()

        // なぜか最初が動かないときがあるので、一度スクロール
        onView(allOf(withId(R.id.list), isDisplayed())).perform(swipeDown())
        waitForIdle()

        for (index in 1..5) {
            var packageName: String = ""
            var label: String = ""
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    val apps = realm.where(App::class.java).findAll()
                    val app = apps[Random().nextInt(apps.size)]
                    packageName = app.packageName
                    label = app.label
                    assertTrue(packageName.isNotEmpty())
                    assertTrue(label.isNotEmpty())
                }
            }
            aplinRule.setMessages(packageName, label)

            intentBlock {
                onData(allOf(hasToString(containsString("{packageName:$packageName}"))))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .perform(click())
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${packageName}"))
            }

            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()
        }
    }

    @LargeTest
    @Test
    fun system() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.SYSTEM)))
                .perform(click())

        waitForIdle()

        // なぜか最初が動かないときがあるので、一度スクロール
        onView(allOf(withId(R.id.list), isDisplayed())).perform(swipeDown())
        waitForIdle()

        for (index in 1..5) {
            var packageName: String = ""
            var label: String = ""
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    val apps = Category.SYSTEM.where(realm.where(App::class.java)).findAll()
                    val app = apps[Random().nextInt(apps.size)]
                    packageName = app.packageName
                    label = app.label
                    assertTrue(packageName.isNotEmpty())
                    assertTrue(label.isNotEmpty())
                }
            }
            aplinRule.setMessages(packageName, label)

            // タップしてみて、Intentを確認
            intentBlock {
                onData(allOf(hasToString(containsString("{packageName:$packageName}"))))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .perform(click())
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${packageName}"))
            }

            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()
        }
    }

    @LargeTest
    @Test
    fun undisablable() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.SYSTEM_UNDISABLABLE)))
                .perform(click())

        waitForIdle()

        // なぜか最初が動かないときがあるので、一度スクロール
        onView(allOf(withId(R.id.list), isDisplayed())).perform(swipeDown())
        waitForIdle()

        for (index in 1..5) {
            var packageName: String = ""
            var label: String = ""
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    val apps = Category.SYSTEM_UNDISABLABLE.where(realm.where(App::class.java)).findAll()
                    val app = apps[Random().nextInt(apps.size)]
                    packageName = app.packageName
                    label = app.label
                    assertTrue(packageName.isNotEmpty())
                    assertTrue(label.isNotEmpty())
                }
            }
            aplinRule.setMessages(packageName, label)

            // タップしてみて、Intentを確認
            intentBlock {
                onData(allOf(hasToString(containsString("{packageName:$packageName}"))))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .perform(click())
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${packageName}"))
            }

            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(label)).waitForExists(timeout))

            val disableButtonExists = uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_disable)).waitForExists(1000)
            val enableButtonExists = !disableButtonExists && uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_enable)).waitForExists(1000)
            assertTrue(disableButtonExists || enableButtonExists)
            if (disableButtonExists) {
                assertFalse(uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_disable)).isEnabled)
            } else {
                assertFalse(uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_enable)).isEnabled)
            }

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()
        }
    }

    @LargeTest
    @Test
    fun disablable() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.SYSTEM_DISABLABLE)))
                .perform(click())

        waitForIdle()

        // なぜか最初が動かないときがあるので、一度スクロール
        onView(allOf(withId(R.id.list), isDisplayed())).perform(swipeDown())
        waitForIdle()

        for (index in 1..5) {
            var packageName: String = ""
            var label: String = ""
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    val apps = Category.SYSTEM_DISABLABLE.where(realm.where(App::class.java)).findAll()
                    val app = apps[Random().nextInt(apps.size)]
                    packageName = app.packageName
                    label = app.label
                    assertTrue(packageName.isNotEmpty())
                    assertTrue(label.isNotEmpty())
                }
            }
            Timber.d("disablable() pkg=%s, label=%s", packageName, label)
            aplinRule.setMessages(packageName, label)

            // タップしてみて、Intentを確認
            intentBlock {
                onData(allOf(hasToString(containsString("{packageName:$packageName}"))))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .atPosition(0)
                        .perform(click())
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${packageName}"))
            }

            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(label)).waitForExists(timeout))

            val disableButtonExists = uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_disable)).waitForExists(1000)
            val enableButtonExists = !disableButtonExists && uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_enable)).waitForExists(1000)
            assertTrue(disableButtonExists || enableButtonExists)
            if (disableButtonExists) {
                assertTrue(uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_disable)).isEnabled)
            } else {
                assertTrue(uiDevice.findObject(UiSelector().textStartsWith(TestResources.string.test_btn_enable)).isEnabled)
            }

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()
        }
    }

    @SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
    @LargeTest
    @Test
    fun runtimePermissions() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.DENIABLE_PERMISSIONS)))
                .perform(click())

        waitForIdle()

        // なぜか最初が動かないときがあるので、一度スクロール
        onView(allOf(withId(R.id.list), isDisplayed())).perform(swipeDown())
        waitForIdle()

        for (index in 1..5) {
            var packageName: String = ""
            var label: String = ""
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    val apps = Category.DENIABLE_PERMISSIONS.where(realm.where(App::class.java)).findAll()
                    val app = apps[Random().nextInt(apps.size)]
                    packageName = app.packageName
                    label = app.label
                    assertTrue(packageName.isNotEmpty())
                    assertTrue(label.isNotEmpty())
                }
            }
            aplinRule.setMessages(packageName, label)

            // タップしてみて、Intentを確認
            intentBlock {
                onData(allOf(hasToString(containsString("{packageName:$packageName}"))))
                        .inAdapterView(allOf(withId(R.id.list), isDisplayed()))
                        .perform(click())
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${packageName}"))
            }

            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(label)).waitForExists(timeout))

            val permissionButton = uiDevice.findObject(UiSelector().textStartsWith("リクエストされた権限はありません")).waitForExists(1000)
            assertFalse(permissionButton)

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()
        }
    }

    private fun waitForIdle() {
        uiDevice.waitForIdle(timeout)
        Thread.sleep(timeout)
    }
}