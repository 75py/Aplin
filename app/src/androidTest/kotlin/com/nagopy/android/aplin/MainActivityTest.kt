package com.nagopy.android.aplin

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.nagopy.android.aplin.presenter.AdPresenter
import com.nagopy.android.aplin.presenter.MainScreenPresenter
import com.nagopy.android.aplin.view.AppListFragment
import com.nagopy.android.aplin.view.MainActivity
import com.nagopy.android.aplin.view.PackageChangedReceiver
import com.nagopy.android.aplin.view.SettingsActivity
import org.assertj.android.api.Assertions
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.hamcrest.CoreMatchers.`is` as _is

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    @Rule
    public fun getActivityTestRule() = rule

    class MockApplicationComponent : ApplicationComponent {

        override fun inject(mainActivity: MainActivity) {
            mainActivity.adPresenter = mock(AdPresenter::class.java, Mockito.RETURNS_DEEP_STUBS)
            mainActivity.presenter = mock(MainScreenPresenter::class.java, Mockito.RETURNS_DEEP_STUBS)

            Mockito.doNothing().`when`(mainActivity.presenter).initialize(Tests.anyObj())
            Mockito.doNothing().`when`(mainActivity.adPresenter).initialize(Tests.anyObj())
        }

        override fun inject(appListFragment: AppListFragment) {
            throw UnsupportedOperationException()
        }

        override fun inject(settingsActivity: SettingsActivity) {
            throw UnsupportedOperationException()
        }

        override fun inject(packageChangedReceiver: PackageChangedReceiver) {
            throw UnsupportedOperationException()
        }
    }

    @Before
    fun before() {
        Aplin.component = MockApplicationComponent()
        rule.launchActivity(null)
    }

    @Test
    public fun testLaunch() {
        val activity = rule.activity
        Assertions.assertThat(activity).isNotNull()
        Assert.assertThat(activity.presenter, _is(notNullValue()))
        Assert.assertThat(activity.adPresenter, _is(notNullValue()))
        Mockito.verify(activity.presenter, Mockito.times(1)).initialize(activity);
        Mockito.verify(activity.adPresenter, Mockito.times(1)).initialize(activity.adView);
    }

}