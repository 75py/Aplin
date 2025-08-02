package com.nagopy.android.aplin.domain.usecase

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import com.nagopy.android.aplin.data.repository.PackageRepository
import com.nagopy.android.aplin.domain.model.PackageModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoadPackagesUseCaseUnitTest {
    private lateinit var packageRepository: PackageRepository
    private lateinit var categorizePackageUseCase: CategorizePackageUseCase
    private lateinit var loadPackagesUseCase: LoadPackagesUseCase

    @Before
    fun setUp() {
        packageRepository = mockk()
        categorizePackageUseCase = mockk()
        loadPackagesUseCase = LoadPackagesUseCase(packageRepository, categorizePackageUseCase)
    }

    @Test
    fun execute_returnsCorrectPackagesModel() = runTest {
        // Given
        val mockPackageInfos = createMockPackageInfos()
        coEvery { packageRepository.loadAll() } returns mockPackageInfos
        coEvery { packageRepository.loadHomePackageNames() } returns setOf("com.android.launcher")
        coEvery { packageRepository.loadCurrentDefaultHomePackageName() } returns "com.android.launcher"
        
        every { categorizePackageUseCase.isDisableable(any(), any(), any()) } returns true
        every { categorizePackageUseCase.isBundled(any()) } returns false

        setupRepositoryMocks()

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        assertTrue("Should return packages", result.allPackages.isNotEmpty())
        assertTrue("Should categorize user packages", result.userPackages.isNotEmpty())
        assertTrue("Should categorize disableable packages", result.disableablePackages.isNotEmpty())
        assertEquals("Should have correct number of packages", 3, result.allPackages.size)
    }

    @Test
    fun execute_sortsPackagesByLabelAndName() = runTest {
        // Given - setup mocks with specific labels for sorting test
        val mockPackageInfos = listOf(
            createMockPackageInfo("com.zzz.app", "ZZZ App"),
            createMockPackageInfo("com.aaa.app", "AAA App"),
            createMockPackageInfo("com.bbb.app", "AAA App") // Same label, different package
        )
        
        setupDefaultMocks(mockPackageInfos)

        // When
        val result = loadPackagesUseCase.execute()

        // Then - verify sorting by label first, then by package name
        val sortedPackages = result.allPackages
        assertEquals("First package should be AAA App with com.aaa.app", "AAA App", sortedPackages[0].label)
        assertEquals("com.aaa.app", sortedPackages[0].packageName)
        assertEquals("Second package should be AAA App with com.bbb.app", "AAA App", sortedPackages[1].label) 
        assertEquals("com.bbb.app", sortedPackages[1].packageName)
        assertEquals("Third package should be ZZZ App", "ZZZ App", sortedPackages[2].label)
    }

    @Test
    fun execute_handlesEmptyRepository() = runTest {
        // Given
        coEvery { packageRepository.loadAll() } returns emptyList()
        coEvery { packageRepository.loadHomePackageNames() } returns emptySet()
        coEvery { packageRepository.loadCurrentDefaultHomePackageName() } returns null

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        assertTrue("All packages should be empty", result.allPackages.isEmpty())
        assertTrue("User packages should be empty", result.userPackages.isEmpty())
        assertTrue("Disableable packages should be empty", result.disableablePackages.isEmpty())
        assertTrue("Disabled packages should be empty", result.disabledPackages.isEmpty())
    }

    @Test
    fun execute_filtersDisabledPackagesCorrectly() = runTest {
        // Given
        val enabledPackage = createMockPackageInfo("com.enabled.app", "Enabled", true)
        val disabledPackage = createMockPackageInfo("com.disabled.app", "Disabled", false)
        
        setupDefaultMocks(listOf(enabledPackage, disabledPackage))

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        assertEquals("Should have one disabled package", 1, result.disabledPackages.size)
        assertEquals("Disabled package should have correct label", "Disabled", result.disabledPackages[0].label)
        assertFalse("Disabled package isEnabled should be false", result.disabledPackages[0].isEnabled)
    }

    @Test 
    fun execute_handlesNullApplicationInfo() = runTest {
        // Given
        val packageWithNullAppInfo = createMockPackageInfoWithNullApp()
        val normalPackage = createMockPackageInfo("com.normal.app", "Normal")
        
        setupDefaultMocks(listOf(packageWithNullAppInfo, normalPackage))

        // When
        val result = loadPackagesUseCase.execute()

        // Then - should filter out null applicationInfo packages
        assertEquals("Should filter out packages with null ApplicationInfo", 1, result.allPackages.size)
        assertEquals("Remaining package should be the normal one", "Normal", result.allPackages[0].label)
    }

    @Test
    fun execute_categorizesPackagesCorrectly() = runTest {
        // Given
        val bundledPackage = createMockPackageInfo("com.bundled.app", "Bundled App")
        val userPackage = createMockPackageInfo("com.user.app", "User App")
        val disableablePackage = createMockPackageInfo("com.disableable.app", "Disableable App")
        
        val packages = listOf(bundledPackage, userPackage, disableablePackage)
        
        coEvery { packageRepository.loadAll() } returns packages
        coEvery { packageRepository.loadHomePackageNames() } returns setOf("com.android.launcher")
        coEvery { packageRepository.loadCurrentDefaultHomePackageName() } returns "com.android.launcher"
        
        // Setup categorization mocks
        every { categorizePackageUseCase.isBundled(bundledPackage) } returns true
        every { categorizePackageUseCase.isBundled(userPackage) } returns false
        every { categorizePackageUseCase.isBundled(disableablePackage) } returns false
        
        every { categorizePackageUseCase.isDisableable(bundledPackage, any(), any()) } returns false
        every { categorizePackageUseCase.isDisableable(userPackage, any(), any()) } returns false
        every { categorizePackageUseCase.isDisableable(disableablePackage, any(), any()) } returns true
        
        setupRepositoryMocks()

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        assertEquals("Should have all packages", 3, result.allPackages.size)
        assertEquals("Should have 2 user packages (non-bundled)", 2, result.userPackages.size)
        assertEquals("Should have 1 disableable package", 1, result.disableablePackages.size)
        assertEquals("Disableable package should be correct", "Disableable App", result.disableablePackages[0].label)
    }

    private fun createMockPackageInfos(): List<PackageInfo> {
        return listOf(
            createMockPackageInfo("com.test.app1", "Test App 1"),
            createMockPackageInfo("com.test.app2", "Test App 2"),
            createMockPackageInfo("com.test.app3", "Test App 3")
        )
    }

    private fun setupDefaultMocks(packageInfos: List<PackageInfo>) {
        coEvery { packageRepository.loadAll() } returns packageInfos
        coEvery { packageRepository.loadHomePackageNames() } returns setOf("com.android.launcher")
        coEvery { packageRepository.loadCurrentDefaultHomePackageName() } returns "com.android.launcher"
        every { categorizePackageUseCase.isDisableable(any(), any(), any()) } returns true
        every { categorizePackageUseCase.isBundled(any()) } returns false
        
        setupRepositoryMocks()
    }

    private fun setupRepositoryMocks() {
        // Mock repository methods for PackageInfo.toPackageModel()
        every { packageRepository.loadLabel(any()) } answers { 
            val appInfo = firstArg<ApplicationInfo>()
            "Label for ${appInfo.packageName}"
        }
        every { packageRepository.loadIcon(any()) } returns mockk<Drawable>(relaxed = true)
    }

    private fun createMockPackageInfo(
        packageName: String,
        label: String,
        enabled: Boolean = true
    ): PackageInfo {
        val packageInfo = mockk<PackageInfo>(relaxed = true)
        val appInfo = mockk<ApplicationInfo>(relaxed = true)
        
        every { packageInfo.packageName } returns packageName
        every { packageInfo.applicationInfo } returns appInfo
        every { packageInfo.firstInstallTime } returns System.currentTimeMillis() - 1000000
        every { packageInfo.lastUpdateTime } returns System.currentTimeMillis()
        every { packageInfo.versionName } returns "1.0.0"
        
        every { appInfo.packageName } returns packageName
        every { appInfo.enabled } returns enabled
        
        return packageInfo
    }

    private fun createMockPackageInfoWithNullApp(): PackageInfo {
        val packageInfo = mockk<PackageInfo>(relaxed = true)
        every { packageInfo.packageName } returns "com.null.app"
        every { packageInfo.applicationInfo } returns null
        return packageInfo
    }
}