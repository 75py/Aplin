package com.nagopy.android.aplin

import android.app.Application
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.nagopy.android.aplin.view.MainActivity
import org.assertj.android.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    @Rule
    public fun getActivityTestRule() = rule

    @Before
    fun before() {
        val targetApp = Mockito.mock(Application::class.java, Mockito.RETURNS_DEEP_STUBS)
        Mockito.`when`(targetApp.getString(R.string.app_name)).thenReturn("test")
        Aplin.component = DaggerApplicationComponent.builder()
                .applicationModule(object : ApplicationModule(targetApp) {
                    override fun provideApplication(): Application {
                        return targetApp
                    }
                    // ここで必要に応じてモックにする
                })
                .build()
        rule.launchActivity(null)
    }

    @Test
    public fun testLaunch() {
        Assertions.assertThat(rule.activity).isNotNull()
    }

    @Test
    fun testInject() {
        assertNotNull(rule.activity.application)
    }

}