package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.SharedPreferences
import androidx.test.filters.SmallTest
import com.nagopy.android.aplin.view.SettingsView
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsPresenterTest {

    lateinit var target: SettingsPresenter

    lateinit var application: Application
    lateinit var sharedPreferences: SharedPreferences
    lateinit var settingsView: SettingsView

    @Before
    fun setup() {
        target = SettingsPresenter()
        application = Mockito.mock(Application::class.java)
        sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        settingsView = Mockito.mock(SettingsView::class.java)

        target.application = application
        target.sharedPreferences = sharedPreferences
    }

    @SmallTest
    @Test
    fun initialize() {
        target.settingChanged = true
        target.initialize(settingsView)
        assertFalse(target.settingChanged)
    }

    @SmallTest
    @Test
    fun resume() {
        Mockito.doNothing().`when`(sharedPreferences).registerOnSharedPreferenceChangeListener(
                ArgumentMatchers.any(SharedPreferences.OnSharedPreferenceChangeListener::class.java))

        target.initialize(settingsView)
        target.resume()

        Mockito.verify(sharedPreferences, Mockito.times(1)).registerOnSharedPreferenceChangeListener(target)
    }

    @SmallTest
    @Test
    fun pause() {
        Mockito.doNothing().`when`(sharedPreferences).unregisterOnSharedPreferenceChangeListener(
                ArgumentMatchers.any(SharedPreferences.OnSharedPreferenceChangeListener::class.java))

        target.pause()

        Mockito.verify(sharedPreferences, Mockito.times(1)).unregisterOnSharedPreferenceChangeListener(target)
    }

    @SmallTest
    @Test
    fun flag() {
        assertFalse(target.settingChanged)
        target.onSharedPreferenceChanged(sharedPreferences, "test")
        assertTrue(target.settingChanged)
    }


    @SmallTest
    @Test
    fun destroy() {
        target.destroy()
    }

    @SmallTest
    @Test
    fun finish_1() {
        target.settingChanged = false
        assertFalse(target.finish())
    }

    @SmallTest
    @Test
    fun finish_2() {
        target.settingChanged = true
        assertTrue(target.finish())
    }
}