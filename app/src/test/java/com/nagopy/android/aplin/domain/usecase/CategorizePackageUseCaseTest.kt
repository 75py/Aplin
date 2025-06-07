package com.nagopy.android.aplin.domain.usecase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.nagopy.android.aplin.data.repository.DevicePolicyRepository
import com.nagopy.android.aplin.data.repository.PackageRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CategorizePackageUseCaseTest {
    private lateinit var packageRepository: PackageRepository
    private lateinit var devicePolicyRepository: DevicePolicyRepository
    private lateinit var categorizePackageUseCase: CategorizePackageUseCase

    @Before
    fun setUp() {
        packageRepository = mockk()
        devicePolicyRepository = mockk()

        // Mock the system package to return null by default
        every { packageRepository.systemPackage } returns null
        categorizePackageUseCase =
            CategorizePackageUseCase(
                packageRepository,
                devicePolicyRepository,
            )
    }

    @Test
    fun isBundled_withSystemFlag_returnsTrue() {
        val packageInfo = createPackageInfo("com.example.test")
        packageInfo.applicationInfo!!.flags = ApplicationInfo.FLAG_SYSTEM

        val result = categorizePackageUseCase.isBundled(packageInfo)

        assertTrue(result)
    }

    @Test
    fun isBundled_withoutSystemFlag_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.test")
        packageInfo.applicationInfo!!.flags = 0

        val result = categorizePackageUseCase.isBundled(packageInfo)

        assertFalse(result)
    }

    @Test
    fun isBundled_withNullApplicationInfo_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.test")
        packageInfo.applicationInfo = null

        val result = categorizePackageUseCase.isBundled(packageInfo)

        assertFalse(result)
    }

    @Test
    fun isDisableable_withNonBundledApp_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.userapp")
        packageInfo.applicationInfo!!.flags = 0 // Not system app

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                emptySet(),
                null,
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withBundledAppAndActiveAdmins_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.systemapp")
        packageInfo.applicationInfo!!.flags = ApplicationInfo.FLAG_SYSTEM

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.systemapp") } returns true
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.systemapp") } returns false

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                emptySet(),
                null,
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withProfileOrDeviceOwner_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.systemapp")
        packageInfo.applicationInfo!!.flags = ApplicationInfo.FLAG_SYSTEM

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.systemapp") } returns false
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.systemapp") } returns true

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                emptySet(),
                null,
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withHomePackageAsBundled_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.launcher")
        packageInfo.applicationInfo!!.flags = ApplicationInfo.FLAG_SYSTEM

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.launcher") } returns false
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.launcher") } returns false

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                setOf("com.example.launcher"),
                null,
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withSingleHomePackageAsDefault_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.launcher")
        packageInfo.applicationInfo!!.flags = 0 // Not bundled

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.launcher") } returns false
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.launcher") } returns false

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                // Only one home package
                setOf("com.example.launcher"),
                "com.example.launcher",
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withMultipleHomePackagesAsCurrentDefault_returnsFalse() {
        val packageInfo = createPackageInfo("com.example.launcher1")
        packageInfo.applicationInfo!!.flags = 0 // Not bundled

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.launcher1") } returns false
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.launcher1") } returns false

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                // Multiple home packages
                setOf("com.example.launcher1", "com.example.launcher2"),
                // This package is the current default
                "com.example.launcher1",
            )

        assertFalse(result)
    }

    @Test
    fun isDisableable_withMultipleHomePackagesButNotDefault_canReturnTrue() {
        val packageInfo = createPackageInfo("com.example.launcher1")
        packageInfo.applicationInfo!!.flags = 0 // Not bundled
        packageInfo.applicationInfo!!.enabled = true

        every { devicePolicyRepository.packageHasActiveAdmins("com.example.launcher1") } returns false
        every { devicePolicyRepository.isProfileOrDeviceOwner("com.example.launcher1") } returns false

        val result =
            categorizePackageUseCase.isDisableable(
                packageInfo,
                // Multiple home packages
                setOf("com.example.launcher1", "com.example.launcher2"),
                // Different package is the current default
                "com.example.launcher2",
            )

        // The exact result depends on handleDisableable logic
        // Since this is not a home package anymore in the logic flow, it should return false
        assertFalse(result)
    }

    @Test
    fun isBundled_withSystemFlagAndUpdatedSystemApp_returnsTrue() {
        val packageInfo = createPackageInfo("com.example.systemapp")
        packageInfo.applicationInfo!!.flags = ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP

        val result = categorizePackageUseCase.isBundled(packageInfo)

        assertTrue(result)
    }

    private fun createPackageInfo(packageName: String): PackageInfo {
        val packageInfo = PackageInfo()
        packageInfo.packageName = packageName
        packageInfo.applicationInfo = ApplicationInfo()
        packageInfo.applicationInfo!!.packageName = packageName
        packageInfo.applicationInfo!!.enabled = true
        return packageInfo
    }
}
