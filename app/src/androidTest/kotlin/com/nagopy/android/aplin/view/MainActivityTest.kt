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

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.support.v7.widget.RecyclerView
import android.test.suitebuilder.annotation.LargeTest
import android.test.suitebuilder.annotation.MediumTest
import android.view.View
import android.widget.ProgressBar
import com.nagopy.android.aplin.AplinTestRule
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.TestFunction.intentBlock
import com.nagopy.android.aplin.model.Analytics
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matcher
import org.junit.*
import timber.log.Timber
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hamcrest.CoreMatchers.`is` as _is

@LargeTest
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    @Rule
    @JvmField
    val aplinRule = AplinTestRule()

    val timeout = 3 * 1000L


    lateinit var uiDevice: UiDevice
    lateinit var sp: SharedPreferences
    lateinit var analytics: Analytics

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        sp = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
        analytics = Analytics(InstrumentationRegistry.getTargetContext().applicationContext as Application, sp)
        analytics.disagree()
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

        val list = onView(allOf(withId(R.id.recyclerView), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            waitForIdle()
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            waitForIdle()
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")
            aplinRule.setMessages(getParams.packageName!!, getParams.label!!)

            // タップしてみて、Intentを確認
            intentBlock {
                list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            }
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
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

        val list = onView(allOf(withId(R.id.recyclerView), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            waitForIdle()
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            waitForIdle()
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")
            aplinRule.setMessages(getParams.packageName!!, getParams.label!!)

            // タップしてみて、Intentを確認
            intentBlock {
                list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            }
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
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

        val list = onView(allOf(withId(R.id.recyclerView), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            waitForIdle()
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            waitForIdle()
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")
            aplinRule.setMessages(getParams.packageName!!, getParams.label!!)

            // タップしてみて、Intentを確認
            intentBlock {
                list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            }
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            val disableButtonExists = uiDevice.findObject(UiSelector().text("無効にする")).waitForExists(1000)
            val enableButtonExists = !disableButtonExists && uiDevice.findObject(UiSelector().text("有効にする")).waitForExists(1000)
            assertTrue(disableButtonExists || enableButtonExists)
            if (disableButtonExists) {
                assertFalse(uiDevice.findObject(UiSelector().text("無効にする")).isEnabled)
            } else {
                assertFalse(uiDevice.findObject(UiSelector().text("有効にする")).isEnabled)
            }

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
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

        val list = onView(allOf(withId(R.id.recyclerView), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            waitForIdle()
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            waitForIdle()
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")
            aplinRule.setMessages(getParams.packageName!!, getParams.label!!)

            // タップしてみて、Intentを確認
            intentBlock {
                list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            }
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            val disableButtonExists = uiDevice.findObject(UiSelector().text("無効にする")).waitForExists(1000)
            val enableButtonExists = !disableButtonExists && uiDevice.findObject(UiSelector().text("有効にする")).waitForExists(1000)
            assertTrue(disableButtonExists || enableButtonExists)
            if (disableButtonExists) {
                assertTrue(uiDevice.findObject(UiSelector().text("無効にする")).isEnabled)
            } else {
                assertTrue(uiDevice.findObject(UiSelector().text("有効にする")).isEnabled)
            }

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
        }
    }

    @Ignore("WRITE_EXTERNAL_STORAGEが正常に判定できない場合があるため保留")
    @LargeTest
    @Test
    fun runtimePermissions() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.DENIABLE_PERMISSIONS)))
                .perform(click())

        waitForIdle()

        val list = onView(allOf(withId(R.id.recyclerView), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            waitForIdle()
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            waitForIdle()
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")
            aplinRule.setMessages(getParams.packageName!!, getParams.label!!)

            // タップしてみて、Intentを確認
            intentBlock {
                list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
                waitForIdle()
                Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            }
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            val permissionButton = uiDevice.findObject(UiSelector().text("リクエストされた権限はありません")).waitForExists(1000)
            assertFalse(permissionButton)

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            waitForIdle()

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
        }
    }

    private fun stopSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.android.settings"))
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        InstrumentationRegistry.getTargetContext().startActivity(intent)
        waitForIdle()
        uiDevice.findObject(UiSelector().text("強制停止")).clickAndWaitForNewWindow(timeout)
        uiDevice.findObject(UiSelector().text("OK")).clickAndWaitForNewWindow(timeout)
    }

    private fun waitForIdle() {
        uiDevice.waitForIdle(timeout)
        Thread.sleep(100)
    }

    class GetParamsAction(val position: Int) : ViewAction {

        var itemCount: Int = 0
        var packageName: String? = null
        var label: String? = null

        override fun getDescription(): String? = "getChildCount"

        override fun getConstraints(): Matcher<View>? {
            return isAssignableFrom(RecyclerView::class.java)
        }

        override fun perform(uiController: UiController?, view: View?) {
            val recyclerView = view as RecyclerView
            itemCount = recyclerView.adapter.itemCount
            if (position < itemCount) {
                val app = (recyclerView.adapter as AppListAdapter).appListPresenter.realmResults[position]
                packageName = app.packageName
                label = app.label
            }
        }

    }
}