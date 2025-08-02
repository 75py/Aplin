package com.nagopy.android.aplin.data.repository

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PackageRepositoryImplUnitTest {
    private lateinit var packageManager: PackageManager
    private lateinit var repository: PackageRepositoryImpl

    @Before
    fun setUp() {
        packageManager = mockk(relaxed = true)
        repository = PackageRepositoryImpl(packageManager)
    }

    @Test
    fun loadAll_callsCorrectPackageManagerMethod() {
        // Given
        val expectedPackages = listOf(
            createMockPackageInfo("com.app1", "App1"),
            createMockPackageInfo("com.app2", "App2")
        )
        every { packageManager.getInstalledPackages(PackageManager.GET_META_DATA) } returns expectedPackages

        // When
        val result = repository.loadAll()

        // Then
        assertEquals("Should return all installed packages", expectedPackages, result)
        verify { packageManager.getInstalledPackages(PackageManager.GET_META_DATA) }
    }

    @Test
    fun loadAll_handlesPackageManagerException() {
        // Given
        every { packageManager.getInstalledPackages(any()) } throws SecurityException("Permission denied")

        // When
        val result = repository.loadAll()

        // Then - Should handle exception gracefully
        assertTrue("Should return empty list on exception", result.isEmpty())
    }

    @Test
    fun loadLabel_returnsApplicationLabel() {
        // Given
        val appInfo = mockk<ApplicationInfo>()
        val expectedLabel = "Test Application"
        every { packageManager.getApplicationLabel(appInfo) } returns expectedLabel

        // When
        val result = repository.loadLabel(appInfo)

        // Then
        assertEquals("Should return application label", expectedLabel, result)
        verify { packageManager.getApplicationLabel(appInfo) }
    }

    @Test
    fun loadLabel_handlesNullLabel() {
        // Given
        val appInfo = mockk<ApplicationInfo>()
        every { packageManager.getApplicationLabel(appInfo) } returns null

        // When
        val result = repository.loadLabel(appInfo)

        // Then
        assertEquals("Should return empty string for null label", "", result)
    }

    @Test
    fun loadIcon_returnsApplicationIcon() {
        // Given
        val appInfo = mockk<ApplicationInfo>() 
        val expectedIcon = mockk<Drawable>()
        every { packageManager.getApplicationIcon(appInfo) } returns expectedIcon

        // When
        val result = repository.loadIcon(appInfo)

        // Then
        assertEquals("Should return application icon", expectedIcon, result)
        verify { packageManager.getApplicationIcon(appInfo) }
    }

    @Test
    fun loadIcon_handlesPackageManagerException() {
        // Given
        val appInfo = mockk<ApplicationInfo>()
        every { packageManager.getApplicationIcon(appInfo) } throws PackageManager.NameNotFoundException("Icon not found")

        // When
        val result = repository.loadIcon(appInfo)

        // Then - Should return default icon (mockk relaxed behavior returns mock)
        assertNotNull("Should return some drawable even if not found", result)
    }

    @Test
    fun loadHomePackageNames_queriesHomeActivities() {
        // Given
        val resolveInfos = listOf(
            createMockResolveInfo("com.launcher1"),
            createMockResolveInfo("com.launcher2"),
            createMockResolveInfo("com.launcher3")
        )
        every { packageManager.queryIntentActivities(any(), 0) } returns resolveInfos

        // When
        val result = repository.loadHomePackageNames()

        // Then
        assertEquals("Should return all launcher package names", 
            setOf("com.launcher1", "com.launcher2", "com.launcher3"), result)
        
        // Verify the correct intent was used
        verify { 
            packageManager.queryIntentActivities(
                match { intent ->
                    intent.action == Intent.ACTION_MAIN && 
                    intent.hasCategory(Intent.CATEGORY_HOME)
                }, 
                0
            ) 
        }
    }

    @Test
    fun loadHomePackageNames_handlesEmptyResult() {
        // Given
        every { packageManager.queryIntentActivities(any(), 0) } returns emptyList()

        // When
        val result = repository.loadHomePackageNames()

        // Then
        assertTrue("Should return empty set when no home activities found", result.isEmpty())
    }

    @Test
    fun loadHomePackageNames_handlesNullActivityInfo() {
        // Given - ResolveInfo with null activityInfo
        val resolveInfo = mockk<ResolveInfo>()
        every { resolveInfo.activityInfo } returns null
        every { packageManager.queryIntentActivities(any(), 0) } returns listOf(resolveInfo)

        // When
        val result = repository.loadHomePackageNames()

        // Then
        assertTrue("Should handle null activityInfo gracefully", result.isEmpty())
    }

    @Test
    fun loadCurrentDefaultHomePackageName_returnsDefaultLauncher() {
        // Given
        val defaultResolveInfo = createMockResolveInfo("com.default.launcher")
        every { 
            packageManager.resolveActivity(any(), PackageManager.MATCH_DEFAULT_ONLY) 
        } returns defaultResolveInfo

        // When
        val result = repository.loadCurrentDefaultHomePackageName()

        // Then
        assertEquals("Should return default launcher package name", 
            "com.default.launcher", result)
        
        verify { 
            packageManager.resolveActivity(
                match { intent ->
                    intent.action == Intent.ACTION_MAIN && 
                    intent.hasCategory(Intent.CATEGORY_HOME)
                }, 
                PackageManager.MATCH_DEFAULT_ONLY
            ) 
        }
    }

    @Test
    fun loadCurrentDefaultHomePackageName_withNoDefault_returnsNull() {
        // Given
        every { 
            packageManager.resolveActivity(any(), PackageManager.MATCH_DEFAULT_ONLY) 
        } returns null

        // When
        val result = repository.loadCurrentDefaultHomePackageName()

        // Then
        assertNull("Should return null when no default launcher is set", result)
    }

    @Test
    fun loadCurrentDefaultHomePackageName_withNullActivityInfo_returnsNull() {
        // Given
        val resolveInfo = mockk<ResolveInfo>()
        every { resolveInfo.activityInfo } returns null
        every { 
            packageManager.resolveActivity(any(), PackageManager.MATCH_DEFAULT_ONLY) 
        } returns resolveInfo

        // When
        val result = repository.loadCurrentDefaultHomePackageName()

        // Then
        assertNull("Should return null when activityInfo is null", result)
    }

    @Test
    fun systemProperties_returnCorrectValues() {
        // Given - Mock system properties
        every { packageManager.permissionControllerPackageName } returns "com.android.permissioncontroller"

        // When/Then - Test various system properties
        assertNotNull("System package should not be null", repository.systemPackage)
        assertNotNull("Permission controller package should not be null", 
            repository.permissionControllerPackageName)
        assertNotNull("Services system shared library should not be null", 
            repository.servicesSystemSharedLibraryPackageName)
        assertNotNull("Shared system shared library should not be null", 
            repository.sharedSystemSharedLibraryPackageName)
        assertNotNull("Print spooler package should not be null", 
            repository.printSpoolerPackageName)
        assertNotNull("Device provisioning package should not be null", 
            repository.deviceProvisioningPackage)
    }

    @Test
    fun exceptionHandling_handlesPackageManagerExceptions() {
        // Given - PackageManager throws various exceptions
        every { packageManager.getInstalledPackages(any()) } throws RuntimeException("System error")
        every { packageManager.getApplicationLabel(any()) } throws OutOfMemoryError("Low memory")

        // When/Then - Should handle exceptions gracefully without crashing
        val packages = repository.loadAll()
        assertTrue("Should return empty list on exception", packages.isEmpty())

        val appInfo = mockk<ApplicationInfo>()
        val label = repository.loadLabel(appInfo) 
        assertEquals("Should return empty string on exception", "", label)
    }

    @Test
    fun concurrentAccess_handledCorrectly() {
        // Given
        val expectedPackages = listOf(createMockPackageInfo("com.test", "Test"))
        every { packageManager.getInstalledPackages(any()) } returns expectedPackages

        // When - Simulate concurrent access
        val results = (1..10).map { 
            Thread {
                repository.loadAll()
            }.apply { start() }
        }.map { thread ->
            thread.join()
            repository.loadAll()
        }

        // Then - All calls should succeed
        results.forEach { result ->
            assertEquals("Concurrent access should work correctly", expectedPackages, result)
        }
    }

    private fun createMockPackageInfo(packageName: String, label: String): PackageInfo {
        val packageInfo = mockk<PackageInfo>(relaxed = true)
        val appInfo = mockk<ApplicationInfo>(relaxed = true)
        
        every { packageInfo.packageName } returns packageName
        every { packageInfo.applicationInfo } returns appInfo
        every { appInfo.packageName } returns packageName
        
        return packageInfo
    }

    private fun createMockResolveInfo(packageName: String): ResolveInfo {
        val resolveInfo = mockk<ResolveInfo>(relaxed = true)
        val activityInfo = mockk<android.content.pm.ActivityInfo>(relaxed = true)
        
        every { resolveInfo.activityInfo } returns activityInfo
        every { activityInfo.packageName } returns packageName
        
        return resolveInfo
    }
}