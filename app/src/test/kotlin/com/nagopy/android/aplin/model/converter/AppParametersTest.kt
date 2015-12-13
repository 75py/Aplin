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

import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import com.nagopy.android.aplin.entity.App
import io.realm.Realm
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(PowerMockRunner::class)
@PrepareForTest(Realm::class)
class AppParametersTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var params: AppConverter.Params

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        params.realm = PowerMockito.mock(Realm::class.java)
    }

    @Test
    fun packageName() {
        params.applicationInfo.packageName = "com.nagopy.android.test"
        val app = App()
        AppParameters.packageName.setValue(app, params)
        assertEquals("com.nagopy.android.test", app.packageName)
    }

    @Test
    fun label() {
        Mockito.`when`(params.applicationInfo.loadLabel(Mockito.any())).thenReturn("Aplin!")
        val app = App()
        AppParameters.label.setValue(app, params)
        assertEquals("Aplin!", app.label)
    }

    @Test
    fun isEnabled() {
        params.applicationInfo.enabled = true
        val app = App()
        AppParameters.isEnabled.setValue(app, params)
        assertTrue(app.isEnabled)
    }

    @Test
    fun isSystem_FLAG_SYSTEM() {
        params.applicationInfo.flags = ApplicationInfo.FLAG_SYSTEM
        val app = App()
        AppParameters.isSystem.setValue(app, params)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_FLAG_UPDATED_SYSTEM_APP() {
        params.applicationInfo.flags = ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        val app = App()
        AppParameters.isSystem.setValue(app, params)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_false() {
        params.applicationInfo.flags = 0
        val app = App()
        AppParameters.isSystem.setValue(app, params)
        assertFalse(app.isSystem)
    }

    @Test
    fun isThisASystemPackage() {
        Mockito.`when`(params.appConverter.aplinDevicePolicyManager.isThisASystemPackage(params.packageInfo)).thenReturn(true)
        val app = App()
        AppParameters.isThisASystemPackage.setValue(app, params)
        assertTrue(app.isThisASystemPackage)
    }

    @Test
    fun firstInstallTime() {
        params.packageInfo.firstInstallTime = 999
        val app = App()
        AppParameters.firstInstallTime.setValue(app, params)
        assertEquals(999, app.firstInstallTime)
    }

    @Test
    fun lastUpdateTime() {
        params.packageInfo.lastUpdateTime = 999
        val app = App()
        AppParameters.lastUpdateTime.setValue(app, params)
        assertEquals(999, app.lastUpdateTime)
    }

    @Test
    fun hasActiveAdmins() {
        params.applicationInfo.packageName = "com.nagopy.android.test"
        Mockito.`when`(params.appConverter.aplinDevicePolicyManager.packageHasActiveAdmins("com.nagopy.android.test")).thenReturn(true)
        val app = App()
        AppParameters.hasActiveAdmins.setValue(app, params)
        assertTrue(app.hasActiveAdmins)
    }

    @Test
    fun isInstalled_true() {
        params.applicationInfo.flags = ApplicationInfo.FLAG_INSTALLED
        val app = App()
        AppParameters.isInstalled.setValue(app, params)
        assertTrue(app.isInstalled)
    }

    @Test
    fun isInstalled_false() {
        params.applicationInfo.flags = 0
        val app = App()
        AppParameters.isInstalled.setValue(app, params)
        assertFalse(app.isInstalled)
    }

    @Test
    fun isDefaultApp_true() {
        Mockito.`when`(params.appConverter.packageManager.getPreferredActivities(Mockito.any(), Mockito.any(), Mockito.any()))
                .then {
                    @Suppress("UNCHECKED_CAST")
                    val outActivities: ArrayList<ComponentName> = it.arguments[1] as ArrayList<ComponentName>
                    outActivities.add(Mockito.mock(ComponentName::class.java))
                    return@then 0
                }

        val app = App()
        AppParameters.isDefaultApp.setValue(app, params)
        assertTrue(app.isDefaultApp)
    }

    @Test
    fun isDefaultApp_false() {
        val app = App()
        AppParameters.isDefaultApp.setValue(app, params)
        assertFalse(app.isDefaultApp)
    }

    @Test
    fun icon_0() {
        params.applicationInfo.icon = 0
        val app = App()
        AppParameters.icon.setValue(app, params)
        assertEquals(params.appConverter.iconHelper.defaultIconByteArray, app.iconByteArray)
    }

    @Test
    fun icon() {
        val mockDrawable = Mockito.mock(Drawable::class.java)
        val mock = ByteArray(1)
        params.applicationInfo.icon = 1
        Mockito.`when`(params.applicationInfo.loadIcon(Mockito.any())).thenReturn(mockDrawable)
        Mockito.`when`(params.appConverter.iconHelper.toByteArray(mockDrawable)).thenReturn(mock)
        val app = App()
        AppParameters.icon.setValue(app, params)
        assertEquals(mock, app.iconByteArray)
    }

    @Test
    fun versionName() {
        params.packageInfo.versionName = "version_x"
        val app = App()
        AppParameters.versionName.setValue(app, params)
        assertEquals("version_x", app.versionName)
    }

    @Test
    fun permissions() {
        // TODO 気力があるときに書く
    }
}