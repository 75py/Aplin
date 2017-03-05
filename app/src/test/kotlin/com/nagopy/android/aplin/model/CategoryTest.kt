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

import android.os.Build
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.entity.PermissionGroup
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CategoryTest {

    @Test
    fun ALL() {
        val list = listOf(App().apply {
            packageName = "aaa"
        }, App().apply {
            packageName = "bbb"
        })
        val filtered = Category.ALL.where(list)
        assertEquals(2, filtered.count())
        assertEquals("aaa", filtered.toMutableList()[0].packageName)
        assertEquals("bbb", filtered.toMutableList()[1].packageName)
    }

    @Test
    fun SYSTEM() {
        val list = listOf(App().apply {
            isSystem = true
            packageName = "aaa"
        }, App().apply {
            isSystem = false
            packageName = "bbb"
        }, App().apply {
            isSystem = true
            packageName = "ccc"
        })
        val filtered = Category.SYSTEM.where(list)
        assertEquals(2, filtered.count())
        assertEquals("aaa", filtered.toMutableList()[0].packageName)
        assertEquals("ccc", filtered.toMutableList()[1].packageName)
    }

    @Test
    fun SYSTEM_UNDISABLABLE() {
        val list = listOf(App().apply {
            isSystem = true
            isProfileOrDeviceOwner = true
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "ProfileOrDeviceOwner"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = true
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "SystemPackage"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = true
            isHomeApp = false
            packageName = "ActiveAdmins"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = true
            packageName = "HomeApp"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "Disablable"
        }, App().apply {
            isSystem = false
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "user"
        }
        )
        val filtered = Category.SYSTEM_UNDISABLABLE.where(list)
        assertEquals(4, filtered.count())
        assertEquals("ProfileOrDeviceOwner", filtered.toMutableList()[0].packageName)
        assertEquals("SystemPackage", filtered.toMutableList()[1].packageName)
        assertEquals("ActiveAdmins", filtered.toMutableList()[2].packageName)
        assertEquals("HomeApp", filtered.toMutableList()[3].packageName)
    }

    @Test
    fun SYSTEM_DISABLABLE() {
        val list = listOf(App().apply {
            isSystem = true
            isProfileOrDeviceOwner = true
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "ProfileOrDeviceOwner"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = true
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "SystemPackage"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = true
            isHomeApp = false
            packageName = "ActiveAdmins"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = true
            packageName = "HomeApp"
        }, App().apply {
            isSystem = true
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "Disablable"
        }, App().apply {
            isSystem = false
            isProfileOrDeviceOwner = false
            isSystemPackage = false
            hasActiveAdmins = false
            isHomeApp = false
            packageName = "user"
        }
        )
        val filtered = Category.SYSTEM_DISABLABLE.where(list)
        assertEquals(1, filtered.count())
        assertEquals("Disablable", filtered.toMutableList()[0].packageName)
    }

    @Test
    fun DISABLED() {
        val list = listOf(App().apply {
            isEnabled = true
            packageName = "enable"
        }, App().apply {
            isEnabled = false
            packageName = "disable"
        })
        val filtered = Category.DISABLED.where(list)
        assertEquals(1, filtered.count())
        assertEquals("disable", filtered.toMutableList()[0].packageName)
    }

    @Test
    fun DEFAULT() {
        val list = listOf(App().apply {
            isDefaultApp = true
            packageName = "DefaultApp"
        }, App().apply {
            isDefaultApp = false
            packageName = "no"
        })
        val filtered = Category.DEFAULT.where(list)
        assertEquals(1, filtered.count())
        assertEquals("DefaultApp", filtered.toMutableList()[0].packageName)
    }

    @Test
    fun USER() {
        val list = listOf(App().apply {
            isSystem = true
            packageName = "System"
        }, App().apply {
            isSystem = false
            packageName = "User"
        })
        val filtered = Category.USER.where(list)
        assertEquals(1, filtered.count())
        assertEquals("User", filtered.toMutableList()[0].packageName)
    }

    @Test
    fun INTERNET_PERMISSIONS() {
        val list = listOf(App().apply {
            requestedPermissions.clear()
            packageName = "Empty"
        }, App().apply {
            requestedPermissions.add(android.Manifest.permission.INTERNET)
            packageName = "Internet1"
        }, App().apply {
            requestedPermissions.add(android.Manifest.permission.INTERNET)
            requestedPermissions.add(android.Manifest.permission.WAKE_LOCK)
            packageName = "Internet2"
        }, App().apply {
            requestedPermissions.add(android.Manifest.permission.WAKE_LOCK)
            packageName = "others"
        })
        val filtered = Category.INTERNET_PERMISSIONS.where(list)
        assertEquals(2, filtered.count())
        assertEquals("Internet1", filtered.toMutableList()[0].packageName)
        assertEquals("Internet2", filtered.toMutableList()[1].packageName)
    }

    @Test
    fun DENIABLE_PERMISSIONS() {
        val list = listOf(App().apply {
            permissionGroups.clear()
            packageName = "Empty"
        }, App().apply {
            permissionGroups.add(PermissionGroup("group1", "group1 label", emptyList()))
            packageName = "Target"
        })
        val filtered = Category.DENIABLE_PERMISSIONS.where(list)
        assertEquals(1, filtered.count())
        assertEquals("Target", filtered.toMutableList()[0].packageName)
    }

    @Test
    fun SYSTEM_ALERT_WINDOW_PERMISSION() {
        val list = listOf(App().apply {
            // false
            isSystemPackage = true
        }, App().apply {
            // false
            isSystemPackage = true
            requestedPermissions.add(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        }, App().apply {
            // false
            isSystemPackage = false
        }, App().apply {
            // false
            isSystemPackage = false
            requestedPermissions.add(android.Manifest.permission.INTERNET)
        }, App().apply {
            // true
            isSystemPackage = false
            requestedPermissions.add(android.Manifest.permission.SYSTEM_ALERT_WINDOW)
        })
        val filtered = Category.SYSTEM_ALERT_WINDOW_PERMISSION.where(list)
        assertEquals(1, filtered.count())
    }

    @Test
    fun getAll() {
        setFinalStatic(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.KITKAT)
        Category.getAll().let {
            assertFalse(it.contains(Category.DENIABLE_PERMISSIONS))
            assertFalse(it.contains(Category.SYSTEM_ALERT_WINDOW_PERMISSION))
        }
        setFinalStatic(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.LOLLIPOP)
        Category.getAll().let {
            assertFalse(it.contains(Category.DENIABLE_PERMISSIONS))
            assertFalse(it.contains(Category.SYSTEM_ALERT_WINDOW_PERMISSION))
        }
        setFinalStatic(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.LOLLIPOP_MR1)
        Category.getAll().let {
            assertFalse(it.contains(Category.DENIABLE_PERMISSIONS))
            assertFalse(it.contains(Category.SYSTEM_ALERT_WINDOW_PERMISSION))
        }
        setFinalStatic(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.M)
        Category.getAll().let {
            assertTrue(it.contains(Category.DENIABLE_PERMISSIONS))
            assertTrue(it.contains(Category.SYSTEM_ALERT_WINDOW_PERMISSION))
        }
    }

    fun setFinalStatic(cls: Class<*>, name: String, newValue: Any) {
        cls.getDeclaredField(name).let {
            it.isAccessible = true

            val modifiersField = Field::class.java.getDeclaredField("modifiers")
            modifiersField.isAccessible = true
            modifiersField.setInt(it, it.modifiers and Modifier.FINAL.inv())

            it.set(null, newValue)
        }
    }

    @Test
    fun resIds() {
        assertEquals(R.string.category_all, Category.ALL.titleResourceId)
        assertEquals(R.string.category_all_summary, Category.ALL.summaryResourceId)
        assertEquals(Constants.ALL_SDK_VERSION, Category.ALL.targetSdkVersion)
    }

}