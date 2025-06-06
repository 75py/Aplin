package com.nagopy.android.aplin.domain.model

import android.graphics.drawable.Drawable
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class PackageModelTest {

    @Test
    fun packageModel_hasCorrectProperties() {
        val mockIcon: Drawable = mockk()
        
        val packageModel = PackageModel(
            packageName = "com.example.test",
            label = "Test App",
            icon = mockIcon,
            isEnabled = true,
            firstInstallTime = 123456789L,
            lastUpdateTime = 987654321L,
            versionName = "1.0.0"
        )

        assertEquals("com.example.test", packageModel.packageName)
        assertEquals("Test App", packageModel.label)
        assertEquals(mockIcon, packageModel.icon)
        assertTrue(packageModel.isEnabled)
        assertEquals(123456789L, packageModel.firstInstallTime)
        assertEquals(987654321L, packageModel.lastUpdateTime)
        assertEquals("1.0.0", packageModel.versionName)
    }

    @Test
    fun packageModel_withNullVersionName_handlesCorrectly() {
        val mockIcon: Drawable = mockk()
        
        val packageModel = PackageModel(
            packageName = "com.example.test",
            label = "Test App",
            icon = mockIcon,
            isEnabled = false,
            firstInstallTime = 0L,
            lastUpdateTime = 0L,
            versionName = null
        )

        assertNull(packageModel.versionName)
        assertFalse(packageModel.isEnabled)
    }
}