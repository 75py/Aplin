package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.SharedPreferences
import android.support.test.runner.AndroidJUnit4
import com.nagopy.android.aplin.model.Apps
import com.nagopy.android.aplin.model.UsageStatsHelper
import com.nagopy.android.aplin.view.SettingsView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.`is` as _is


@RunWith(AndroidJUnit4::class)
class SettingsPresenterTest {

    lateinit var target: SettingsPresenter

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var sharedPreferences: SharedPreferences
    @Mock
    lateinit var apps: Apps
    @Mock
    lateinit var settingsView: SettingsView
    @Mock
    lateinit var usageStatsHelper: UsageStatsHelper

    @Before
    fun setup() {
        target = SettingsPresenter()
        MockitoAnnotations.initMocks(this)
        target.application = application
        target.sharedPreferences = sharedPreferences
        target.apps = apps
        target.usageStatsHelper = usageStatsHelper
    }

    @Test
    fun initialize() {
        target.settingChanged = true
        target.initialize(settingsView)
        Assert.assertThat(target.settingChanged, _is(false))
    }

    @Test
    fun resume() {
        Mockito.doNothing().`when`(sharedPreferences).registerOnSharedPreferenceChangeListener(
                Matchers.any(SharedPreferences.OnSharedPreferenceChangeListener::class.java))

        target.initialize(settingsView)
        target.resume()

        Mockito.verify(sharedPreferences, Mockito.times(1)).registerOnSharedPreferenceChangeListener(target)
    }

    @Test
    fun pause() {
        Mockito.doNothing().`when`(sharedPreferences).unregisterOnSharedPreferenceChangeListener(
                Matchers.any(SharedPreferences.OnSharedPreferenceChangeListener::class.java))

        target.pause()

        Mockito.verify(sharedPreferences, Mockito.times(1)).unregisterOnSharedPreferenceChangeListener(target)
    }

    @Test
    fun flag() {
        Assert.assertThat(target.settingChanged, _is(false))
        target.onSharedPreferenceChanged(null, null)
        Assert.assertThat(target.settingChanged, _is(true))
    }


    @Test
    fun destroy() {
        target.destroy()
    }

    @Test
    fun finish() {
        target.settingChanged = false
        Assert.assertThat(target.finish(), _is(false))
        Mockito.verify(apps, Mockito.times(0)).invalidateCache()

        target.settingChanged = true
        Assert.assertThat(target.finish(), _is(true))
        Mockito.verify(apps, Mockito.times(1)).invalidateCache()
    }
}