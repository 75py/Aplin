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
import android.content.pm.PackageInfo
import com.nagopy.android.aplin.entity.App
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(PowerMockRunner::class)
class AppParametersTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var appConverter: AppConverter

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var packageInfo: PackageInfo

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var applicationInfo: ApplicationInfo

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        packageInfo.applicationInfo = applicationInfo
    }

    @Test
    fun packageName() {
        packageInfo.applicationInfo.packageName = "com.nagopy.android.test"
        val app = App()
        AppParameters.packageName.setValue(app, packageInfo, appConverter)
        assertEquals("com.nagopy.android.test", app.packageName)
    }

    @Test
    fun label() {
        Mockito.`when`(packageInfo.applicationInfo.loadLabel(Mockito.any())).thenReturn("Aplin!")
        val app = App()
        AppParameters.label.setValue(app, packageInfo, appConverter)
        assertEquals("Aplin!", app.label)
    }

    @Test
    fun isEnabled() {
        packageInfo.applicationInfo.enabled = true
        val app = App()
        AppParameters.isEnabled.setValue(app, packageInfo, appConverter)
        assertTrue(app.isEnabled)
    }

    @Test
    fun isSystem_FLAG_SYSTEM() {
        packageInfo.applicationInfo.flags = ApplicationInfo.FLAG_SYSTEM
        val app = App()
        AppParameters.isSystem.setValue(app, packageInfo, appConverter)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_FLAG_UPDATED_SYSTEM_APP() {
        packageInfo.applicationInfo.flags = ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
        val app = App()
        AppParameters.isSystem.setValue(app, packageInfo, appConverter)
        assertTrue(app.isSystem)
    }

    @Test
    fun isSystem_false() {
        packageInfo.applicationInfo.flags = 0
        val app = App()
        AppParameters.isSystem.setValue(app, packageInfo, appConverter)
        assertFalse(app.isSystem)
    }

    @Test
    fun isThisASystemPackage() {
        Mockito.`when`(appConverter.aplinDevicePolicyManager.isSystemPackage(packageInfo)).thenReturn(true)
        val app = App()
        AppParameters.isSystemPackage.setValue(app, packageInfo, appConverter)
        assertTrue(app.isSystemPackage)
    }

    @Test
    fun firstInstallTime() {
        packageInfo.firstInstallTime = 999
        val app = App()
        AppParameters.firstInstallTime.setValue(app, packageInfo, appConverter)
        assertEquals(999, app.firstInstallTime)
    }

    @Test
    fun lastUpdateTime() {
        packageInfo.lastUpdateTime = 999
        val app = App()
        AppParameters.lastUpdateTime.setValue(app, packageInfo, appConverter)
        assertEquals(999, app.lastUpdateTime)
    }

    @Test
    fun hasActiveAdmins() {
        packageInfo.applicationInfo.packageName = "com.nagopy.android.test"
        Mockito.`when`(appConverter.aplinDevicePolicyManager.packageHasActiveAdmins("com.nagopy.android.test")).thenReturn(true)
        val app = App()
        AppParameters.hasActiveAdmins.setValue(app, packageInfo, appConverter)
        assertTrue(app.hasActiveAdmins)
    }

    @Test
    fun isInstalled_true() {
        packageInfo.applicationInfo.flags = ApplicationInfo.FLAG_INSTALLED
        val app = App()
        AppParameters.isInstalled.setValue(app, packageInfo, appConverter)
        assertTrue(app.isInstalled)
    }

    @Test
    fun isInstalled_false() {
        packageInfo.applicationInfo.flags = 0
        val app = App()
        AppParameters.isInstalled.setValue(app, packageInfo, appConverter)
        assertFalse(app.isInstalled)
    }

    @Test
    fun isDefaultApp_true() {
        Mockito.`when`(appConverter.packageManager.getPreferredActivities(Mockito.any(), Mockito.any(), Mockito.any()))
                .then {
                    @Suppress("UNCHECKED_CAST")
                    val outActivities: ArrayList<ComponentName> = it.arguments[1] as ArrayList<ComponentName>
                    outActivities.add(Mockito.mock(ComponentName::class.java))
                    return@then 0
                }

        val app = App()
        AppParameters.isDefaultApp.setValue(app, packageInfo, appConverter)
        assertTrue(app.isDefaultApp)
    }

    @Test
    fun isDefaultApp_false() {
        val app = App()
        AppParameters.isDefaultApp.setValue(app, packageInfo, appConverter)
        assertFalse(app.isDefaultApp)
    }

    @Test
    fun versionName() {
        packageInfo.versionName = "version_x"
        val app = App()
        AppParameters.versionName.setValue(app, packageInfo, appConverter)
        assertEquals("version_x", app.versionName)
    }

    @Test
    fun permissions() {
        // TODO 気力があるときに書く
    }
}