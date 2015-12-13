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

import android.app.Application
import android.content.pm.ApplicationInfo
import android.support.test.InstrumentationRegistry
import android.test.suitebuilder.annotation.SmallTest
import com.nagopy.android.aplin.*
import com.nagopy.android.aplin.entity.App
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SmallTest
class AppConverterTest {

    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application

    @Inject
    lateinit var appConverter: AppConverter

    lateinit var aplinApplicationInfo: ApplicationInfo

    lateinit var realm: Realm

    lateinit var aplinApp: App

    @Before
    fun setup() {
        Aplin.component = DaggerApplicationMockComponent.builder()
                .applicationMockModule(ApplicationMockModule(application))
                .build()

        (Aplin.getApplicationComponent() as ApplicationMockComponent).inject(this)
        realm = Realm.getInstance(RealmConfiguration.Builder(application)
                .name(javaClass.name)
                .inMemory()
                .build())

        val packageManager = application.packageManager
        packageManager.getInstalledApplications(0).forEach {
            if (it.packageName == application.packageName) {
                aplinApplicationInfo = it
            }
        }

        aplinApp = App()
        realm.executeTransaction {
            appConverter.setValues(realm, aplinApp, aplinApplicationInfo)
        }
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun packageName() {
        assertEquals(InstrumentationRegistry.getTargetContext().packageName, aplinApp.packageName)
    }

    @Test
    fun label() {
        assertEquals(application.getString(R.string.app_name), aplinApp.label)
    }

    @Test
    fun isEnabled() {
        assertEquals(true, aplinApp.isEnabled)
    }

    @Test
    fun isSystem() {
        assertEquals(false, aplinApp.isSystem)
    }

    @Test
    fun isThisASystemPackage() {
        assertEquals(false, aplinApp.isThisASystemPackage)
    }

    @Test
    fun firstInstallTime() {
        assert(aplinApp.firstInstallTime > 0)
    }

    @Test
    fun lastUpdateTime() {
        assert(aplinApp.lastUpdateTime > 0)
    }

    @Test
    fun hasActiveAdmins() {
        assertEquals(false, aplinApp.hasActiveAdmins)
    }

    @Test
    fun isInstalled() {
        assertEquals(true, aplinApp.isInstalled)
    }

    @Test
    fun isDefaultApp() {
        assertEquals(false, aplinApp.hasActiveAdmins)
    }

    @Test
    fun icon() {
        assertNotNull(aplinApp.iconByteArray)
    }

    @Test
    fun versionName() {
        assertEquals(BuildConfig.VERSION_NAME, aplinApp.versionName)
    }

    @Test
    fun permissions() {
        assertEquals(true, aplinApp.permissions.isNotEmpty())
    }
}
