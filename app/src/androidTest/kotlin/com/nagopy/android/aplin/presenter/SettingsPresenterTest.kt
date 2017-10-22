package com.nagopy.android.aplin.presenter

import android.app.Application
import android.content.SharedPreferences
import android.support.test.filters.SmallTest
import com.nagopy.android.aplin.view.SettingsView
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
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