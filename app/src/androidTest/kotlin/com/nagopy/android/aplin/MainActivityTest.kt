package com.nagopy.android.aplin

import android.app.Application
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import org.assertj.android.api.Assertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Rule
    public fun getActivityTestRule() = rule

    @Before
    fun before() {
        val mock = Mockito.mock(Application::class.java)
        Aplin.component = DaggerApplicationComponent.builder()
                .applicationModule(object : ApplicationModule(mock) {
                    override fun provideApplication(): Application {
                        return mock
                    }
                    // ここで必要に応じてモックにする
                })
                .build()
    }

    @Test
    public fun testLaunch() {
        Assertions.assertThat(rule.activity).isNotNull()
    }

    @Test
    public fun testText() {
        val textView = rule.activity.findViewById(R.id.text) as TextView
        Assertions.assertThat(textView).isVisible()
        Assertions.assertThat(textView).hasText(R.string.app_name)
    }

    @Test
    fun testInject() {
        assertNotNull(rule.activity.app)
    }

}