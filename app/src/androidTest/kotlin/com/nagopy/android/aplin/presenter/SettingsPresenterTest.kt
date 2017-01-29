package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.SharedPreferences
import com.nagopy.android.aplin.view.SettingsView
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.hamcrest.CoreMatchers.`is` as _is

class SettingsPresenterTest {

    lateinit var target: SettingsPresenter

    @Mock
    lateinit var application: Application
    @Mock
    lateinit var sharedPreferences: SharedPreferences
    @Mock
    lateinit var settingsView: SettingsView

    @Before
    fun setup() {
        target = SettingsPresenter()
        MockitoAnnotations.initMocks(this)
        target.application = application
        target.sharedPreferences = sharedPreferences
    }

    @Test
    fun initialize() {
        target.settingChanged = true
        target.initialize(settingsView)
        assertFalse(target.settingChanged)
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
        assertFalse(target.settingChanged)
        target.onSharedPreferenceChanged(sharedPreferences, "test")
        assertTrue(target.settingChanged)
    }


    @Test
    fun destroy() {
        target.destroy()
    }

    @Test
    fun finish_1() {
        target.settingChanged = false
        assertFalse(target.finish())
    }

    @Test
    fun finish_2() {
        target.settingChanged = true
        assertTrue(target.finish())
    }
}