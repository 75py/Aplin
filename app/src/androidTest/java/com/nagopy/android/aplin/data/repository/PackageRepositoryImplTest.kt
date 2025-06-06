package com.nagopy.android.aplin.data.repository

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PackageRepositoryImplTest {
    private lateinit var packageManager: PackageManager
    private lateinit var packRepositoryImpl: PackageRepositoryImpl

    @Before
    fun setUp() {
        packageManager = mockk(relaxed = true)
        packRepositoryImpl = PackageRepositoryImpl(packageManager)
    }

    @Test
    fun getSystemPackage() {
        assertNotNull(packRepositoryImpl.systemPackage)
    }

    @Test
    fun getPermissionControllerPackageName() {
        assertNotNull(packRepositoryImpl.permissionControllerPackageName)
    }

    @Test
    fun getServicesSystemSharedLibraryPackageName() {
        assertNotNull(packRepositoryImpl.servicesSystemSharedLibraryPackageName)
    }

    @Test
    fun getSharedSystemSharedLibraryPackageName() {
        assertNotNull(packRepositoryImpl.sharedSystemSharedLibraryPackageName)
    }

    @Test
    fun getPrintSpoolerPackageName() {
        assertNotNull(packRepositoryImpl.printSpoolerPackageName)
    }

    @Test
    fun getDeviceProvisioningPackage() {
        assertNotNull(packRepositoryImpl.deviceProvisioningPackage)
    }
}
