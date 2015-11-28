/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.aplin.model.converter

import android.content.pm.ApplicationInfo
import com.nagopy.android.aplin.entity.App
import org.junit.Before
import org.junit.Test
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppParametersTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var appInfo: AppConverter.AppInfo
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var envInfo: AppConverter.EnvInfo

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun packageName() {
        appInfo.applicationInfo.packageName = "com.nagopy.android.test"
        val app = App()
        AppParameters.packageName.setValue(app, appInfo, envInfo)
        assertEquals("com.nagopy.android.test", app.packageName)
    }

    @Test
    fun label() {
        Mockito.`when`(appInfo.applicationInfo.loadLabel(Mockito.any())).thenReturn("Aplin!")
        val app = App()
        AppParameters.label.setValue(app, appInfo, envInfo)
        assertEquals("Aplin!", app.label)
    }

    @Test
    fun isEnabled() {
        appInfo.applicationInfo.enabled = true
        val app = App()
        AppParameters.isEnabled.setValue(app, appInfo, envInfo)
        assertTrue(app.isEnabled)
    }

    @Test
    fun isSystem_FLAG_SYSTEM() {
        appInfo.applicationInfo.flags = ApplicationInfo.FLAG_SYSTEM
        val app = App()
        AppParameters.isSystem.setValue(app, appInfo, envInfo)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_FLAG_UPDATED_SYSTEM_APP() {
        appInfo.applicationInfo.flags = ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        val app = App()
        AppParameters.isSystem.setValue(app, appInfo, envInfo)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_false() {
        appInfo.applicationInfo.flags = 0
        val app = App()
        AppParameters.isSystem.setValue(app, appInfo, envInfo)
        assertFalse(app.isSystem)
    }

    @Test
    fun isThisASystemPackage() {
        Mockito.`when`(envInfo.appConverter.devicePolicy.isThisASystemPackage(appInfo.packageInfo)).thenReturn(true)
        val app = App()
        AppParameters.isThisASystemPackage.setValue(app, appInfo, envInfo)
        assertTrue(app.isThisASystemPackage)
    }

    @Test
    fun firstInstallTime() {
        appInfo.packageInfo.firstInstallTime = 999
        val app = App()
        AppParameters.firstInstallTime.setValue(app, appInfo, envInfo)
        assertEquals(999, app.firstInstallTime)
    }

    @Test
    fun lastUpdateTime() {
        appInfo.packageInfo.lastUpdateTime = 999
        val app = App()
        AppParameters.lastUpdateTime.setValue(app, appInfo, envInfo)
        assertEquals(999, app.lastUpdateTime)
    }

    @Test
    fun hasActiveAdmins() {
        appInfo.applicationInfo.packageName = "com.nagopy.android.test"
        Mockito.`when`(envInfo.appConverter.devicePolicy.packageHasActiveAdmins("com.nagopy.android.test")).thenReturn(true)
        val app = App()
        AppParameters.hasActiveAdmins.setValue(app, appInfo, envInfo)
        assertTrue(app.hasActiveAdmins)
    }

    @Test
    fun isInstalled_true() {
        appInfo.applicationInfo.flags = ApplicationInfo.FLAG_INSTALLED
        val app = App()
        AppParameters.isInstalled.setValue(app, appInfo, envInfo)
        assertTrue(app.isInstalled)
    }

    @Test
    fun isInstalled_false() {
        appInfo.applicationInfo.flags = 0
        val app = App()
        AppParameters.isInstalled.setValue(app, appInfo, envInfo)
        assertFalse(app.isInstalled)
    }

    @Test
    fun isDefaultApp() {
        val app = App()
        AppParameters.isDefaultApp.setValue(app, appInfo, envInfo)
        assertFalse(app.isDefaultApp)

        // TODO trueのときのテスト
    }

    // TODO
}