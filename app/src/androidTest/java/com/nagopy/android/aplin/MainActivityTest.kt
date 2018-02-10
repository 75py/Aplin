package com.nagopy.android.aplin

import android.provider.Settings
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nagopy.android.aplin.loader.AppInfo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    lateinit var uiDevice: UiDevice

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    private fun toResName(resId: Int): String {
        return InstrumentationRegistry.getTargetContext().resources.getResourceName(resId)
    }

    private fun start() {
        rule.launchActivity(null)

        assertThat(
                uiDevice.wait(Until.hasObject(By.res(toResName(R.id.tabLayout))), 30000)
                , `is`(true)
        )
        assertThat(
                uiDevice.wait(Until.hasObject(By.res(toResName(R.id.recyclerView))), 5000)
                , `is`(true)
        )
    }

    private fun selectCategory(category: Category) {
        onView(allOf(isDescendantOfA(withId(R.id.tabLayout)), withText(category.titleResId)))
                .perform(click())
    }

    // https://stackoverflow.com/questions/36399787/how-to-count-recyclerview-items-with-espresso
    private fun getCountFromRecyclerView(recyclerViewId: Int): Int {
        var count: Int? = null
        val matcher = object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View?): Boolean {
                count = (item as? RecyclerView)?.adapter?.itemCount
                return true;
            }

            override fun describeTo(description: Description?) {
            }
        }
        onView(allOf(withId(recyclerViewId), isDisplayed())).check(matches(matcher));
        return count ?: 0
    }

    private fun getAppInfo(position: Int): AppInfo? {
        var vh: AppListFragment.AppViewHolder? = null
        val matcher = object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View?): Boolean {
                vh = (item as? RecyclerView)?.findViewHolderForAdapterPosition(position) as? AppListFragment.AppViewHolder
                return true;
            }

            override fun describeTo(description: Description?) {
            }
        }
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).check(matches(matcher));
        return vh?.binding?.appInfo
    }

    private fun intentBlock(func: () -> Unit) {
        try {
            Intents.init()
            func()
        } finally {
            Intents.release()
        }
    }

    private fun getString(resId: Int): String {
        return InstrumentationRegistry.getContext().getString(resId)
    }

    @Test
    fun test_all() {
        start()

        selectCategory(Category.ALL)

        val itemCount = getCountFromRecyclerView(R.id.recyclerView)
        for (i in 0 until itemCount) {
            uiDevice.waitForIdle()

            // scroll
            onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                    .perform(RecyclerViewActions.actionOnItemAtPosition<AppListFragment.AppViewHolder>(i, scrollTo()))

            // get item
            val appInfo = getAppInfo(i)!!
            Timber.d("%s", appInfo)

            // click
            intentBlock {
                onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                        .perform(RecyclerViewActions.actionOnItemAtPosition<AppListFragment.AppViewHolder>(i, click()))

                uiDevice.waitForIdle()

                Intents.intended(allOf(
                        IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        , IntentMatchers.hasData("package:${appInfo.packageName}")
                ))
            }
            uiDevice.pressBack()
            uiDevice.waitForIdle()
        }

    }

    @Test
    fun test_disablable() {
        start()

        selectCategory(Category.DISABLABLE)

        val itemCount = getCountFromRecyclerView(R.id.recyclerView)
        for (i in 0 until itemCount) {
            uiDevice.waitForIdle()

            // scroll
            onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                    .perform(RecyclerViewActions.actionOnItemAtPosition<AppListFragment.AppViewHolder>(i, scrollTo()))

            // get item
            val appInfo = getAppInfo(i)!!
            Timber.d("%s", appInfo)

            // click
            intentBlock {
                onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                        .perform(RecyclerViewActions.actionOnItemAtPosition<AppListFragment.AppViewHolder>(i, click()))

                uiDevice.waitForIdle()

                Intents.intended(allOf(
                        IntentMatchers.hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        , IntentMatchers.hasData("package:${appInfo.packageName}")
                ))
            }

            com.nagopy.android.aplin.test.R.string.test_btn_disable
            val disableButtonLabel = getString(
                    if (appInfo.isEnabled) {
                        com.nagopy.android.aplin.test.R.string.test_btn_disable
                    } else {
                        com.nagopy.android.aplin.test.R.string.test_btn_enable
                    })
            Timber.d(disableButtonLabel)
            assertTrue(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).waitForExists(3000), appInfo.toString())
            assertTrue(uiDevice.findObject(UiSelector().textStartsWith(disableButtonLabel)).isEnabled, appInfo.toString())

            uiDevice.pressBack()
            uiDevice.waitForIdle()
        }

    }


}