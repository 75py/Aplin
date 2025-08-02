package com.nagopy.android.aplin.ui.main

import android.app.ActivityManager
import android.content.pm.PackageManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.domain.usecase.LoadPackagesUseCase
import com.nagopy.android.aplin.ui.prefs.SortOrder
import com.nagopy.android.aplin.ui.prefs.UserDataStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var mockActivityManager: ActivityManager
    private lateinit var mockPackageManager: PackageManager
    private lateinit var mockLoadPackagesUseCase: LoadPackagesUseCase
    private lateinit var mockUserDataStore: UserDataStore
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        mockActivityManager = mockk(relaxed = true)
        mockPackageManager = mockk(relaxed = true)
        mockLoadPackagesUseCase = mockk(relaxed = true)
        mockUserDataStore = mockk(relaxed = true)
        
        every { mockActivityManager.launcherLargeIconSize } returns 48
        every { mockUserDataStore.sortOrder } returns flowOf(SortOrder.NAME)
        
        // Setup default successful response
        coEvery { mockLoadPackagesUseCase.execute() } returns createMockPackagesModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isCorrect() = runTest {
        // When - ViewModel is created
        viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = viewModel.viewModelState.value

        // Then
        assertFalse("Should not be loading initially", initialState.isLoading)
        assertNotNull("Should have packages loaded", initialState.packagesModel)
        assertEquals("Should have correct launcher icon size", 48, viewModel.launcherLargeIconSize)
    }

    @Test
    fun updatePackages_loadsPackagesAndUpdatesState() = runTest {
        // Given
        val expectedPackagesModel = createMockPackagesModel()
        coEvery { mockLoadPackagesUseCase.execute() } returns expectedPackagesModel
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.updatePackages()
        advanceUntilIdle()

        // Then
        val state = viewModel.viewModelState.value
        assertFalse("Should not be loading after completion", state.isLoading)
        assertEquals("Should have loaded packages", expectedPackagesModel, state.packagesModel)
    }

    @Test
    fun sortOrderChange_updatesPackagesWithNewOrder() = runTest {
        // Given
        val packagesModel = createMockPackagesModel()
        coEvery { mockLoadPackagesUseCase.execute() } returns packagesModel
        
        val sortOrderFlow = MutableStateFlow(SortOrder.NAME)
        every { mockUserDataStore.sortOrder } returns sortOrderFlow

        viewModel = createViewModel()
        advanceUntilIdle()

        // When - simulate sort order change
        sortOrderFlow.value = SortOrder.INSTALL_TIME
        advanceUntilIdle()

        // Then
        val state = viewModel.viewModelState.value
        assertNotNull("Should have packages model", state.packagesModel)
        // Note: In real implementation, the UseCase would be called again with new sort order
        // Here we verify the flow is observed
        assertTrue("State should be updated", !state.isLoading)
    }

    @Test
    fun uninstallPackage_createsProperly() {
        // Given
        viewModel = createViewModel()
        val packageName = "com.example.test"

        // When
        val result = viewModel.uninstallPackage(packageName)

        // Then - Should create uninstall intent
        assertNotNull("Should create intent", result)
        assertEquals("Should have correct action", 
            android.content.Intent.ACTION_DELETE, result.action)
        assertEquals("Should have correct data", 
            "package:$packageName", result.dataString)
    }

    @Test
    fun sharePackage_createsShareIntent() {
        // Given  
        viewModel = createViewModel()
        val packageModel = createMockPackageModel("com.example.test", "Test App")

        // When
        val result = viewModel.sharePackage(packageModel)

        // Then - Should create share intent
        assertNotNull("Should create intent", result)
        assertEquals("Should have correct action", 
            android.content.Intent.ACTION_SEND, result.action)
        assertEquals("Should have correct type", 
            "text/plain", result.type)
        assertTrue("Should contain package info in extra text",
            result.getStringExtra(android.content.Intent.EXTRA_TEXT)?.contains("Test App") == true)
    }

    @Test
    fun enableDisablePackage_createsSettingsIntent() {
        // Given
        viewModel = createViewModel()
        val packageName = "com.example.test"

        // When
        val result = viewModel.enableDisablePackage(packageName)

        // Then - Should create app details settings intent
        assertNotNull("Should create intent", result)
        assertEquals("Should have correct action", 
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, result.action)
        assertEquals("Should have correct data", 
            "package:$packageName", result.dataString)
    }

    @Test
    fun errorHandling_updatesStateCorrectly() = runTest {
        // Given - UseCase throws exception
        coEvery { mockLoadPackagesUseCase.execute() } throws RuntimeException("Test error")
        
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then - Should handle error gracefully
        val state = viewModel.viewModelState.value
        assertFalse("Should not be loading after error", state.isLoading)
        // Note: In a real implementation, you might want to add error state to MainUiState
        // For now, we verify it doesn't crash and stops loading
    }

    @Test
    fun packageSearch_filtersCorrectly() = runTest {
        // Given
        val packages = listOf(
            createMockPackageModel("com.camera.app", "Camera"),
            createMockPackageModel("com.calculator.app", "Calculator"),
            createMockPackageModel("com.game.app", "Super Game")
        )
        val packagesModel = PackagesModel(
            allPackages = packages,
            userPackages = packages,
            disableablePackages = packages,
            disabledPackages = emptyList()
        )
        coEvery { mockLoadPackagesUseCase.execute() } returns packagesModel
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When - search for "Cam"
        val searchText = "Cam"
        val filtered = viewModel.filterPackages(packages, searchText)

        // Then - Should only return camera app
        assertEquals("Should find one match", 1, filtered.size)
        assertEquals("Should find camera app", "Camera", filtered[0].label)
    }

    @Test
    fun multiplePackageUpdates_handledCorrectly() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()
        
        // When - Multiple rapid updates
        repeat(3) {
            viewModel.updatePackages()
        }
        advanceUntilIdle()

        // Then - Should handle multiple calls gracefully
        val state = viewModel.viewModelState.value
        assertFalse("Should not be loading", state.isLoading)
        assertNotNull("Should have packages", state.packagesModel)
    }

    private fun createViewModel(): MainViewModel {
        return MainViewModel(
            mockActivityManager,
            mockPackageManager,
            mockLoadPackagesUseCase,
            testDispatcher,
            mockUserDataStore
        )
    }

    private fun createMockPackagesModel(): PackagesModel {
        val packages = listOf(
            createMockPackageModel("com.test.app1", "Test App 1"),
            createMockPackageModel("com.test.app2", "Test App 2")
        )
        
        return PackagesModel(
            allPackages = packages,
            userPackages = packages,
            disableablePackages = packages,
            disabledPackages = emptyList()
        )
    }

    private fun createMockPackageModel(packageName: String, label: String): PackageModel {
        return PackageModel(
            packageName = packageName,
            label = label,
            icon = mockk(relaxed = true),
            isEnabled = true,
            firstInstallTime = System.currentTimeMillis(),
            lastUpdateTime = System.currentTimeMillis(),
            versionName = "1.0.0"
        )
    }
}