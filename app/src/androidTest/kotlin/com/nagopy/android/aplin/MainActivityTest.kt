package com.nagopy.android.aplin

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.filters.FlakyTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.Until
import android.view.View
import android.widget.TableLayout
import com.nagopy.android.aplin.model.Analytics
import com.nagopy.android.aplin.view.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    lateinit var uiDevice: UiDevice
    lateinit var sp: SharedPreferences
    lateinit var analytics: Analytics

    @Rule
    fun getActivityTestRule() = rule

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        sp = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
        analytics = Analytics(InstrumentationRegistry.getTargetContext().applicationContext as Application, sp)
    }

    private fun startActivity() {
        rule.launchActivity(null)
    }

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

    @FlakyTest
    @Test
    fun all() {
        analytics.agree()
        startActivity()

        val activity = rule.activity
        uiDevice.wait(Until.hasObject(By.clazz(TableLayout::class.java)), 10000)

        assertEquals(View.GONE, activity.progressBar.visibility)
    }

}