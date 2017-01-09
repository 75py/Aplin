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

package com.nagopy.android.aplin.model

import android.app.Application
import android.support.test.InstrumentationRegistry
import android.test.suitebuilder.annotation.SmallTest
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.entity.AppPermission
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmList
import org.junit.After
import org.junit.Before
import org.junit.Test

@SmallTest
class CategoryTest {

    val application = InstrumentationRegistry.getTargetContext().applicationContext as Application

    lateinit var realm: Realm

    @Before
    fun setup() {
        Realm.init(application)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .name(javaClass.name)
                .inMemory()
                .build())
        Realm.getDefaultInstance().use {
            it.where(App::class.java).findAll().deleteAllFromRealm()
        }
        realm = Realm.getDefaultInstance()
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun overlay_systemApp() {
        realm.executeTransaction {
            val app = it.createObject(App::class.java, "test")
            app.label = "isSystemPackage = true"
            app.isSystemPackage = true
        }
        val result = Category.SYSTEM_ALERT_WINDOW_PERMISSION.where(realm.where(App::class.java)).findAll()

        assert(result.isEmpty())
    }


    @Test
    fun overlay_noPermissions() {
        realm.executeTransaction {
            val app = it.createObject(App::class.java, "test")
            app.label = "isSystemPackage = false , permission = empty"
            app.isSystemPackage = false
            app.permissions = RealmList()
        }
        val result = Category.SYSTEM_ALERT_WINDOW_PERMISSION.where(realm.where(App::class.java)).findAll()

        assert(result.isEmpty())
    }

    @Test
    fun overlay() {
        realm.executeTransaction {
            val app = it.createObject(App::class.java, "test")
            app.label = "isSystemPackage = false , permission = SYSTEM_ALERT_WINDOW"
            app.isSystemPackage = false
            app.permissions = RealmList()
            val p = it.createObject(AppPermission::class.java)
            p.name = android.Manifest.permission.SYSTEM_ALERT_WINDOW
            app.permissions.add(p)
        }
        val result = Category.SYSTEM_ALERT_WINDOW_PERMISSION.where(realm.where(App::class.java)).findAll()

        assert(result.isNotEmpty())
    }

}