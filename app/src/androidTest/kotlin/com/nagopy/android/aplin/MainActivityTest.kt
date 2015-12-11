package com.nagopy.android.aplin

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
import android.support.test.filters.RequiresDevice
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.nagopy.android.aplin.model.Analytics
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.view.MainActivity
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    val timeout = 60 * 1000L


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

    private fun startActivity(): MainActivity = rule.launchActivity(null)

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

    @RequiresDevice
    @Test
    fun all() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.ALL)))
                .perform(click())

        uiDevice.waitForIdle(timeout)

        val list = onView(allOf(withId(R.id.list), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            uiDevice.waitForIdle(timeout)
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            uiDevice.waitForIdle(timeout)
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")

            // タップしてみて、Intentを確認
            Intents.init()
            list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
            uiDevice.waitForIdle(timeout)
            Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
            Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            Intents.release()
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            uiDevice.waitForIdle(timeout)

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
        }
    }

    @RequiresDevice
    @Test
    fun system() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.SYSTEM)))
                .perform(click())

        uiDevice.waitForIdle(timeout)

        val list = onView(allOf(withId(R.id.list), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            uiDevice.waitForIdle(timeout)
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            uiDevice.waitForIdle(timeout)
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")

            // タップしてみて、Intentを確認
            Intents.init()
            list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
            uiDevice.waitForIdle(timeout)
            Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
            Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            Intents.release()
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            uiDevice.waitForIdle(timeout)

            if (index % 50 == 49) {
                // なぜか100件を越えると不安定になるので、50件ごとにアプリを終了させる
                stopSettings()
            }
        }
    }

    @RequiresDevice
    @Test
    fun disablable() {
        startActivity()

        uiDevice.wait(Until.gone(By.clazz(ProgressBar::class.java)), timeout)

        onView(withId(R.id.spinner)).perform(click())
        onData(allOf(_is(instanceOf(Category::class.java)), _is(Category.SYSTEM_DISABLABLE)))
                .perform(click())

        uiDevice.waitForIdle(timeout)

        val list = onView(allOf(withId(R.id.list), isDisplayed()))
        val gpa = GetParamsAction(0)
        list.perform(gpa)
        var lastIndex = gpa.itemCount - 1
        for (index in 0..lastIndex) {
            // 件数、パッケージ名・アプリ名を取得
            uiDevice.waitForIdle(timeout)
            val getParams = GetParamsAction(index)
            list.perform(getParams)
            uiDevice.waitForIdle(timeout)
            Timber.d("$index/${getParams.itemCount} ${getParams.packageName} ${getParams.label}")

            // タップしてみて、Intentを確認
            Intents.init()
            list.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(index, click()))
            uiDevice.waitForIdle(timeout)
            Intents.intended(IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
            Intents.intended(IntentMatchers.hasData("package:${getParams.packageName}"))
            Intents.release()
            // ラベル名が表示されていることをuiautomatorで確認
            assertTrue(uiDevice.findObject(UiSelector().text(getParams.label)).waitForExists(timeout))

            val disableButtonExists = uiDevice.findObject(UiSelector().text("無効にする")).waitForExists(1000)
            val enableButtonExists = uiDevice.findObject(UiSelector().text("有効にする")).waitForExists(1000)
            assertTrue(disableButtonExists || enableButtonExists)
            if (disableButtonExists) {
                assertTrue(uiDevice.findObject(UiSelector().text("無効にする")).isEnabled)
            } else {
                assertTrue(uiDevice.findObject(UiSelector().text("有効にする")).isEnabled)
            }

            // 設定画面からバックキーで戻る
            uiDevice.pressBack()
            uiDevice.waitForIdle(timeout)

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
        uiDevice.waitForIdle(timeout)
        uiDevice.findObject(UiSelector().text("強制停止")).clickAndWaitForNewWindow(timeout)
        uiDevice.findObject(UiSelector().text("OK")).clickAndWaitForNewWindow(timeout)
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