package com.nagopy.android.aplin.data.repository

import android.app.admin.DevicePolicyManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DevicePolicyRepositoryImplTest {

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var devicePolicyRepositoryImpl: DevicePolicyRepositoryImpl

    @Before
    fun setUp() {
        devicePolicyManager = mockk(relaxed = true)
        devicePolicyRepositoryImpl = DevicePolicyRepositoryImpl(devicePolicyManager)
    }

    @Test
    fun packageHasActiveAdmins() {
        assertNotNull(devicePolicyRepositoryImpl.packageHasActiveAdmins)
    }

    @Test
    fun isProfileOrDeviceOwner() {
        val pkg = "foo"
        every { devicePolicyManager.isDeviceOwnerApp(pkg) }.returns(false)
        every { devicePolicyManager.isProfileOwnerApp(pkg) }.returns(true)

        assertTrue(devicePolicyRepositoryImpl.isProfileOrDeviceOwner(pkg))
        verify { devicePolicyManager.isDeviceOwnerApp(pkg) }
        verify { devicePolicyManager.isProfileOwnerApp(pkg) }
    }
}
