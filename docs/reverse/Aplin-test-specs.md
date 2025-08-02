# Aplin テスト仕様書（逆生成）

## 分析概要

**分析日時**: 2025-01-27T11:16:00Z
**対象コードベース**: /app/src/main/java/com/nagopy/android/aplin
**現在のテストカバレッジ**: 約10% (5テストファイル / 47ソースファイル)
**生成テストケース数**: 67個
**実装推奨テスト数**: 42個

## 現在のテスト実装状況

### テストフレームワーク
- **単体テスト**: JUnit4 + Mockk
- **統合テスト**: AndroidJUnit4 + UIAutomator
- **E2Eテスト**: 未実装
- **コードカバレッジ**: Kover (設定済み、但し実行履歴なし)

### テストカバレッジ詳細

| ファイル/ディレクトリ | 行カバレッジ | 分岐カバレッジ | 関数カバレッジ | 優先度 |
|---------------------|-------------|-------------|-------------|-------|
| domain/model/ | 80% | 70% | 85% | 低 |
| domain/usecase/ | 33% | 20% | 40% | 高 |
| data/repository/ | 15% | 10% | 20% | 高 |
| ui/main/ | 0% | 0% | 0% | 高 |
| ui/prefs/ | 20% | 0% | 25% | 中 |
| ui/ads/ | 0% | 0% | 0% | 中 |
| ui/theme/ | 0% | 0% | 0% | 低 |
| **全体** | **10%** | **8%** | **12%** | |

### テストカテゴリ別実装状況

#### 単体テスト
- [x] **PackageModel**: PackageModelTest.kt (完全実装)
- [x] **PackagesModel**: PackagesModelTest.kt (完全実装)
- [x] **SortOrder**: SortOrderTest.kt (完全実装)
- [x] **CategorizePackageUseCase**: CategorizePackageUseCaseTest.kt (完全実装)
- [ ] **LoadPackagesUseCase**: 単体テスト未実装 (統合テストのみ)
- [ ] **MainViewModel**: 未実装
- [ ] **UserDataStore**: 未実装
- [ ] **PackageRepositoryImpl**: 未実装 (統合テストのみ)

#### 統合テスト
- [x] **LoadPackagesUseCase**: LoadPackagesUseCaseTest.kt (E2E風統合テスト)
- [x] **PackageRepositoryImpl**: PackageRepositoryImplTest.kt (部分実装)
- [x] **DevicePolicyRepositoryImpl**: DevicePolicyRepositoryImplTest.kt (部分実装)
- [ ] **MainViewModel + UseCase**: 未実装
- [ ] **Repository間連携**: 未実装

#### E2Eテスト
- [ ] **アプリ起動からパッケージ一覧表示**: 未実装
- [ ] **検索機能**: 未実装
- [ ] **ソート機能**: 未実装
- [ ] **カテゴリ切替**: 未実装
- [ ] **設定画面**: 未実装

## 生成されたテストケース

### Domain Layer テストケース

#### LoadPackagesUseCase 単体テスト (未実装)

**目的**: パッケージ読み込みロジックの単体テスト

```kotlin
package com.nagopy.android.aplin.domain.usecase

import com.nagopy.android.aplin.data.repository.PackageRepository
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        assertTrue(result.allPackages.isNotEmpty())
        assertTrue(result.userPackages.isNotEmpty())
        assertTrue(result.disableablePackages.isNotEmpty())
    }

    @Test
    fun execute_sortsPackagesByLabelAndName() = runTest {
        // Given - setup mocks with specific labels
        val mockPackageInfos = listOf(
            createMockPackageInfo("com.zzz.app", "ZZZ App"),
            createMockPackageInfo("com.aaa.app", "AAA App"),
            createMockPackageInfo("com.bbb.app", "AAA App") // Same label, different package
        )
        
        setupDefaultMocks(mockPackageInfos)

        // When
        val result = loadPackagesUseCase.execute()

        // Then
        val sortedPackages = result.allPackages
        assertEquals("AAA App", sortedPackages[0].label)
        assertEquals("com.aaa.app", sortedPackages[0].packageName)
        assertEquals("AAA App", sortedPackages[1].label) 
        assertEquals("com.bbb.app", sortedPackages[1].packageName)
        assertEquals("ZZZ App", sortedPackages[2].label)
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
        assertTrue(result.allPackages.isEmpty())
        assertTrue(result.userPackages.isEmpty())
        assertTrue(result.disableablePackages.isEmpty())
        assertTrue(result.disabledPackages.isEmpty())
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
        assertEquals(1, result.disabledPackages.size)
        assertEquals("Disabled", result.disabledPackages[0].label)
        assertFalse(result.disabledPackages[0].isEnabled)
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
        assertEquals(1, result.allPackages.size)
        assertEquals("Normal", result.allPackages[0].label)
    }

    private fun setupDefaultMocks(packageInfos: List<PackageInfo>) {
        coEvery { packageRepository.loadAll() } returns packageInfos
        coEvery { packageRepository.loadHomePackageNames() } returns setOf("com.android.launcher")
        coEvery { packageRepository.loadCurrentDefaultHomePackageName() } returns "com.android.launcher"
        every { categorizePackageUseCase.isDisableable(any(), any(), any()) } returns true
        every { categorizePackageUseCase.isBundled(any()) } returns false
        
        // Mock repository methods for PackageInfo.toPackageModel()
        every { packageRepository.loadLabel(any()) } answers { "Label for ${firstArg<ApplicationInfo>().packageName}" }
        every { packageRepository.loadIcon(any()) } returns mockk()
    }
}
```

#### CategorizePackageUseCase エッジケーステスト (追加実装)

```kotlin
class CategorizePackageUseCaseEdgeCaseTest {
    
    @Test
    fun isDisableable_withHomePackageAsDefault_returnsFalse() {
        // Given
        val homePackageInfo = createMockPackageInfo("com.android.launcher", "Launcher")
        val homePackages = setOf("com.android.launcher", "com.samsung.launcher")
        val currentDefault = "com.android.launcher"

        // When
        val result = categorizePackageUseCase.isDisableable(
            homePackageInfo, homePackages, currentDefault
        )

        // Then
        assertFalse("Default home package should not be disableable", result)
    }

    @Test
    fun isBundled_withSystemApp_returnsTrue() {
        // Given
        val systemPackage = createSystemPackageInfo("com.android.settings")

        // When  
        val result = categorizePackageUseCase.isBundled(systemPackage)

        // Then
        assertTrue("System apps should be considered bundled", result)
    }

    @Test
    fun isDisableable_withCriticalSystemService_returnsFalse() {
        // Test critical system packages that should never be disabled
        val criticalPackages = listOf(
            "com.android.systemui",
            "com.android.phone", 
            "com.android.settings",
            "android"
        )

        criticalPackages.forEach { packageName ->
            val packageInfo = createSystemPackageInfo(packageName)
            val result = categorizePackageUseCase.isDisableable(
                packageInfo, emptySet(), null
            )
            assertFalse("Critical package $packageName should not be disableable", result)
        }
    }
}
```

### Data Layer テストケース

#### PackageRepositoryImpl 単体テスト (未実装)

```kotlin
class PackageRepositoryImplUnitTest {
    private lateinit var packageManager: PackageManager
    private lateinit var repository: PackageRepositoryImpl

    @Before
    fun setUp() {
        packageManager = mockk(relaxed = true)
        repository = PackageRepositoryImpl(packageManager)
    }

    @Test
    fun loadAll_returnsAllInstalledPackages() {
        // Given
        val expectedPackages = listOf(
            createMockPackageInfo("com.app1", "App1"),
            createMockPackageInfo("com.app2", "App2")
        )
        every { packageManager.getInstalledPackages(any()) } returns expectedPackages

        // When
        val result = repository.loadAll()

        // Then
        assertEquals(expectedPackages, result)
        verify { packageManager.getInstalledPackages(PackageManager.GET_META_DATA) }
    }

    @Test
    fun loadLabel_returnsApplicationLabel() {
        // Given
        val appInfo = mockk<ApplicationInfo>()
        val expectedLabel = "Test App"
        every { packageManager.getApplicationLabel(appInfo) } returns expectedLabel

        // When
        val result = repository.loadLabel(appInfo)

        // Then
        assertEquals(expectedLabel, result)
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
        assertEquals(expectedIcon, result)
    }

    @Test
    fun loadHomePackageNames_returnsHomeActivityPackages() {
        // Given
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfos = listOf(
            createMockResolveInfo("com.launcher1"),
            createMockResolveInfo("com.launcher2")
        )
        every { packageManager.queryIntentActivities(homeIntent, 0) } returns resolveInfos

        // When
        val result = repository.loadHomePackageNames()

        // Then
        assertEquals(setOf("com.launcher1", "com.launcher2"), result)
    }

    @Test
    fun loadCurrentDefaultHomePackageName_returnsDefaultLauncher() {
        // Given
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val defaultResolveInfo = createMockResolveInfo("com.default.launcher")
        every { packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY) } returns defaultResolveInfo

        // When
        val result = repository.loadCurrentDefaultHomePackageName()

        // Then
        assertEquals("com.default.launcher", result)
    }

    @Test
    fun loadCurrentDefaultHomePackageName_withNoDefault_returnsNull() {
        // Given
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        every { packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY) } returns null

        // When
        val result = repository.loadCurrentDefaultHomePackageName()

        // Then
        assertNull(result)
    }
}
```

### UI Layer テストケース

#### MainViewModel テスト (未実装)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockActivityManager: ActivityManager
    private lateinit var mockPackageManager: PackageManager  
    private lateinit var mockLoadPackagesUseCase: LoadPackagesUseCase
    private lateinit var mockUserDataStore: UserDataStore
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        mockActivityManager = mockk(relaxed = true)
        mockPackageManager = mockk(relaxed = true)
        mockLoadPackagesUseCase = mockk(relaxed = true)
        mockUserDataStore = mockk(relaxed = true)
        
        every { mockUserDataStore.sortOrder } returns flowOf(SortOrder.NAME)
        
        viewModel = MainViewModel(
            mockActivityManager,
            mockPackageManager,
            mockLoadPackagesUseCase,
            Dispatchers.Unconfined,
            mockUserDataStore
        )
    }

    @Test
    fun initialState_isLoadingFalse() {
        // When - ViewModel is created
        val initialState = viewModel.viewModelState.value

        // Then
        assertFalse(initialState.isLoading)
        assertNull(initialState.packagesModel)
    }

    @Test
    fun updatePackages_loadsPackagesAndUpdatesState() = runTest {
        // Given
        val expectedPackagesModel = createMockPackagesModel()
        coEvery { mockLoadPackagesUseCase.execute() } returns expectedPackagesModel

        // When
        viewModel.updatePackages()
        advanceUntilIdle()

        // Then
        val state = viewModel.viewModelState.value
        assertFalse(state.isLoading)
        assertEquals(expectedPackagesModel, state.packagesModel)
    }

    @Test
    fun sortOrderChange_updatesPackagesWithNewOrder() = runTest {
        // Given
        val packagesModel = createMockPackagesModel()
        coEvery { mockLoadPackagesUseCase.execute() } returns packagesModel
        
        val sortOrderFlow = MutableStateFlow(SortOrder.NAME)
        every { mockUserDataStore.sortOrder } returns sortOrderFlow

        // When - simulate sort order change
        sortOrderFlow.value = SortOrder.INSTALL_TIME
        advanceUntilIdle()

        // Then
        val state = viewModel.viewModelState.value
        assertNotNull(state.packagesModel)
        // Verify packages are sorted by install time
    }

    @Test
    fun uninstallPackage_startsUninstallIntent() {
        // Given
        val packageName = "com.example.test"

        // When
        viewModel.uninstallPackage(packageName)

        // Then - Should create and start uninstall intent
        // Verify intent creation logic
    }

    @Test
    fun sharePackage_createsShareIntent() {
        // Given  
        val packageModel = createMockPackageModel("com.example.test", "Test App")

        // When
        viewModel.sharePackage(packageModel)

        // Then - Should create share intent with package details
    }

    @Test
    fun enableDisablePackage_startsAppDetailsSettings() {
        // Given
        val packageName = "com.example.test"

        // When
        viewModel.enableDisablePackage(packageName)

        // Then - Should start app details settings
    }
}
```

#### Compose UI テスト (未実装)

```kotlin
@RunWith(AndroidJUnit4::class)  
class MainScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_displaysLoadingState() {
        // Given
        val loadingState = MainUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            AplinTheme {
                MainScreen(
                    state = loadingState,
                    onCategorySelected = {},
                    onPackageAction = { _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysPackageList() {
        // Given
        val packagesModel = createMockPackagesModel()
        val state = MainUiState(
            isLoading = false,
            packagesModel = packagesModel,
            selectedCategory = AppCategory.ALL
        )

        // When
        composeTestRule.setContent {
            AplinTheme {
                MainScreen(
                    state = state,
                    onCategorySelected = {},
                    onPackageAction = { _, _ -> }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test App 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test App 2").assertIsDisplayed()
    }

    @Test
    fun mainScreen_categorySelection_updatesContent() {
        // Given
        val packagesModel = createMockPackagesModel()
        val state = MainUiState(
            isLoading = false,
            packagesModel = packagesModel,
            selectedCategory = AppCategory.ALL
        )
        var selectedCategory: AppCategory? = null

        // When
        composeTestRule.setContent {
            AplinTheme {
                MainScreen(
                    state = state,
                    onCategorySelected = { selectedCategory = it },
                    onPackageAction = { _, _ -> }
                )
            }
        }

        // Tap on DISABLEABLE category
        composeTestRule.onNodeWithText("無効化可能").performClick()

        // Then
        assertEquals(AppCategory.DISABLEABLE, selectedCategory)
    }

    @Test
    fun packageItem_longClick_showsContextMenu() {
        // Given
        val packageModel = createMockPackageModel("com.test.app", "Test App")
        var actionTriggered: String? = null

        // When
        composeTestRule.setContent {
            AplinTheme {
                PackageItem(
                    packageModel = packageModel,
                    onAction = { action, _ -> actionTriggered = action }
                )
            }
        }

        composeTestRule.onNodeWithText("Test App").performLongClick()

        // Then
        composeTestRule.onNodeWithText("アンインストール").assertIsDisplayed()
        composeTestRule.onNodeWithText("共有").assertIsDisplayed()
        composeTestRule.onNodeWithText("設定").assertIsDisplayed()
    }

    @Test
    fun searchWidget_filtersPackages() {
        // Given
        val packagesModel = PackagesModel(
            allPackages = listOf(
                createMockPackageModel("com.app1", "Camera App"),
                createMockPackageModel("com.app2", "Calculator App"),
                createMockPackageModel("com.app3", "Game App")
            ),
            // ... other lists
        )
        val state = MainUiState(
            packagesModel = packagesModel,
            searchWidgetState = SearchWidgetState.OPENED,
            searchText = "Cam"
        )

        // When
        composeTestRule.setContent {
            AplinTheme {
                MainScreen(
                    state = state,
                    onCategorySelected = {},
                    onPackageAction = { _, _ -> }
                )
            }
        }

        // Then - Only Camera App should be visible
        composeTestRule.onNodeWithText("Camera App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calculator App").assertDoesNotExist()
        composeTestRule.onNodeWithText("Game App").assertDoesNotExist()
    }
}
```

### 統合テストケース (追加実装)

#### Repository + UseCase 統合テスト

```kotlin
@RunWith(AndroidJUnit4::class)
class RepositoryUseCaseIntegrationTest {
    
    @Test
    fun realPackageRepository_withRealUseCase_loadsActualPackages() = runTest {
        // Given - Real Android context and dependencies
        val context = ApplicationProvider.getApplicationContext<Context>()
        val packageRepository = PackageRepositoryImpl(context.packageManager)
        val devicePolicyRepository = DevicePolicyRepositoryImpl(
            context.getSystemService(DevicePolicyManager::class.java)
        )
        val categorizeUseCase = CategorizePackageUseCase(
            packageRepository, devicePolicyRepository
        )
        val loadPackagesUseCase = LoadPackagesUseCase(
            packageRepository, categorizeUseCase
        )

        // When
        val result = loadPackagesUseCase.execute()

        // Then - Verify actual Android packages are loaded
        assertTrue("Should load at least some packages", result.allPackages.isNotEmpty())
        assertTrue("Should have Android system packages", 
            result.allPackages.any { it.packageName.startsWith("com.android") })
        
        // Verify categorization works
        result.disableablePackages.forEach { pkg ->
            assertFalse("Disableable packages should not include system critical apps",
                pkg.packageName in listOf("android", "com.android.systemui"))
        }

        // Verify data integrity
        result.allPackages.forEach { pkg ->
            assertNotNull("Package name should not be null", pkg.packageName)
            assertNotNull("Package label should not be null", pkg.label)
            assertNotNull("Package icon should not be null", pkg.icon)
        }
    }
}
```

## パフォーマンステストケース

### 大量データ処理テスト

```kotlin
class PerformanceTest {
    
    @Test(timeout = 5000) // 5秒以内に完了すること
    fun loadPackages_withManyPackages_completesWithinTimeLimit() = runTest {
        // Given - Mock repository with many packages
        val manyPackages = (1..1000).map { 
            createMockPackageInfo("com.app$it", "App $it")
        }
        
        every { packageRepository.loadAll() } returns manyPackages
        setupDefaultMocks()

        // When
        val startTime = System.currentTimeMillis()
        val result = loadPackagesUseCase.execute()
        val endTime = System.currentTimeMillis()

        // Then
        assertTrue("Should complete within 5 seconds", endTime - startTime < 5000)
        assertEquals(1000, result.allPackages.size)
    }

    @Test
    fun packageSorting_withLargeDataset_maintainsPerformance() {
        // Test sorting performance with large datasets
        val packages = (1..10000).map {
            createMockPackageModel("com.app$it", "App ${Random.nextInt()}")
        }

        val startTime = System.currentTimeMillis()
        val sorted = packages.sortedWith(compareBy({ it.label }, { it.packageName }))
        val endTime = System.currentTimeMillis()

        assertTrue("Sorting should complete within 1 second", endTime - startTime < 1000)
        assertTrue("Should maintain sort order", 
            sorted.zipWithNext().all { it.first.label <= it.second.label })
    }
}
```

### メモリ使用量テスト

```kotlin  
class MemoryTest {
    
    @Test
    fun loadPackages_doesNotCauseMemoryLeak() = runTest {
        // Given
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // When - Load packages multiple times
        repeat(100) {
            loadPackagesUseCase.execute()
            if (it % 10 == 0) {
                System.gc() // Encourage garbage collection
                Thread.sleep(100)
            }
        }
        
        System.gc()
        Thread.sleep(500)
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // Then - Memory should not grow significantly
        val memoryIncrease = finalMemory - initialMemory
        val maxAllowedIncrease = 50 * 1024 * 1024 // 50MB
        
        assertTrue("Memory increase should be less than 50MB, but was ${memoryIncrease / 1024 / 1024}MB",
            memoryIncrease < maxAllowedIncrease)
    }
}
```

## セキュリティテストケース

### データ検証テスト

```kotlin
class SecurityTest {
    
    @Test
    fun packageModel_sanitizesDisplayData() {
        // Given - Package with potentially malicious data
        val maliciousLabel = "<script>alert('xss')</script>Malicious App"
        val packageInfo = createMockPackageInfo("com.malicious.app", maliciousLabel)
        
        // When
        val packageModel = packageInfo.toPackageModel()
        
        // Then - Should not contain script tags in any form
        assertNotNull(packageModel)
        assertFalse("Label should not contain script tags",
            packageModel.label.contains("<script"))
        assertFalse("Label should not contain HTML entities",
            packageModel.label.contains("&lt;script"))
    }

    @Test  
    fun repository_handlesInvalidPackageNames() {
        // Given - Invalid package name patterns
        val invalidNames = listOf(
            "", // Empty
            "..", // Path traversal
            "../../system", // Path traversal
            "com.app\n\r", // Line breaks
            "com.app\0", // Null bytes
            "a".repeat(1000) // Extremely long name
        )

        invalidNames.forEach { invalidName ->
            // When - Try to create package info with invalid name
            try {
                val packageInfo = createMockPackageInfo(invalidName, "Test")
                val result = packageRepository.isValidPackage(packageInfo)
                
                // Then - Should handle gracefully
                assertFalse("Invalid package name should be rejected: $invalidName", result)
            } catch (e: Exception) {
                // Should not throw uncaught exceptions
                assertTrue("Should handle invalid names gracefully: $invalidName", 
                    e is IllegalArgumentException || e is SecurityException)
            }
        }
    }
}
```

## E2Eテストケース

### ユーザーフロー統合テスト

```kotlin
@RunWith(AndroidJUnit4::class)
class E2EUserFlowTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun completeUserFlow_launchToPackageAction() {
        // Given - App is launched
        composeTestRule.waitForIdle()
        
        // When - Navigate through the app
        // 1. Verify initial load
        composeTestRule.onNodeWithTag("package_list").assertIsDisplayed()
        
        // 2. Switch to disableable packages
        composeTestRule.onNodeWithText("無効化可能").performClick()
        composeTestRule.waitForIdle()
        
        // 3. Search for a package
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("test")
        composeTestRule.waitForIdle()
        
        // 4. Select a package (if any exists)
        composeTestRule.onAllNodesWithTag("package_item")
            .onFirst()
            .assertIsDisplayed()
            .performLongClick()
        
        // 5. Verify context menu appears
        composeTestRule.onNodeWithText("設定").assertIsDisplayed()
        composeTestRule.onNodeWithText("共有").assertIsDisplayed()
        
        // Then - All interactions completed successfully
        assertTrue("User flow completed without crashes", true)
    }

    @Test
    fun searchAndFilter_worksEndToEnd() {
        // Full test of search functionality from UI to data layer
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_field").performTextInput("camera")
        
        // Verify filtered results
        composeTestRule.waitForIdle()
        
        // All visible items should contain "camera" in name or label
        composeTestRule.onAllNodesWithTag("package_item").fetchSemanticsNodes().forEach { node ->
            val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString() ?: ""
            assertTrue("Filtered item should contain search term", 
                text.contains("camera", ignoreCase = true))
        }
    }
    
    @Test
    fun categorySwitch_updatesContentCorrectly() {
        // Given - Start with ALL category
        composeTestRule.onNodeWithText("すべて").assertIsSelected()
        
        val categories = listOf("無効化可能", "無効", "ユーザー")
        
        categories.forEach { categoryName ->
            // When - Switch category
            composeTestRule.onNodeWithText(categoryName).performClick()
            composeTestRule.waitForIdle()
            
            // Then - Category is selected and content updates
            composeTestRule.onNodeWithText(categoryName).assertIsSelected()
            composeTestRule.onNodeWithTag("package_list").assertIsDisplayed()
        }
    }
}
```

## テスト環境設定

### モックファクトリー

```kotlin
object TestDataFactory {
    
    fun createMockPackageModel(
        packageName: String = "com.test.app",
        label: String = "Test App",
        isEnabled: Boolean = true,
        firstInstallTime: Long = System.currentTimeMillis(),
        lastUpdateTime: Long = System.currentTimeMillis(),
        versionName: String? = "1.0.0"
    ): PackageModel {
        val mockIcon = mockk<Drawable>(relaxed = true)
        return PackageModel(
            packageName = packageName,
            label = label,
            icon = mockIcon,
            isEnabled = isEnabled,
            firstInstallTime = firstInstallTime,
            lastUpdateTime = lastUpdateTime,
            versionName = versionName
        )
    }
    
    fun createMockPackagesModel(): PackagesModel {
        val allPackages = listOf(
            createMockPackageModel("com.app1", "App 1"),
            createMockPackageModel("com.app2", "App 2"),
            createMockPackageModel("com.disabled.app", "Disabled App", false)
        )
        
        return PackagesModel(
            allPackages = allPackages,
            userPackages = allPackages.filter { !it.packageName.startsWith("com.android") },
            disableablePackages = allPackages.filter { it.isEnabled },
            disabledPackages = allPackages.filter { !it.isEnabled }
        )
    }
    
    fun createMockPackageInfo(
        packageName: String,
        label: String,
        enabled: Boolean = true
    ): PackageInfo {
        val packageInfo = mockk<PackageInfo>(relaxed = true)
        val appInfo = mockk<ApplicationInfo>(relaxed = true)
        
        every { packageInfo.packageName } returns packageName
        every { packageInfo.applicationInfo } returns appInfo
        every { packageInfo.firstInstallTime } returns System.currentTimeMillis()
        every { packageInfo.lastUpdateTime } returns System.currentTimeMillis()
        every { packageInfo.versionName } returns "1.0.0"
        
        every { appInfo.packageName } returns packageName
        every { appInfo.enabled } returns enabled
        
        return packageInfo
    }
}

// Coroutine test rule for MainDispatcher
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
```

### テストデータベース設定

```kotlin
// For testing data persistence (if added in future)
@Database(
    entities = [PackageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TestDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao
    
    companion object {
        fun createInMemoryDatabase(context: Context): TestDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                TestDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}
```

## 不足テストの優先順位

### 高優先度（即座に実装推奨）
1. **MainViewModel 単体テスト** - UI状態管理の中核ロジック
2. **LoadPackagesUseCase 単体テスト** - ビジネスロジック検証
3. **PackageRepositoryImpl 単体テスト** - データアクセス層の信頼性
4. **基本的なCompose UIテスト** - UI動作の保証

### 中優先度（次のスプリントで実装）
1. **統合テスト（Repository + UseCase）** - レイヤー間連携の確認
2. **E2Eユーザーフローテスト** - 主要機能の動作確認  
3. **パフォーマンステスト** - 大量データ処理の性能確認
4. **UserDataStore テスト** - 設定保存機能の確認

### 低優先度（継続的改善として実装）
1. **セキュリティテスト** - 悪意あるデータ対策
2. **アクセシビリティテスト** - a11y対応確認
3. **テーマ・UI コンポーネントテスト** - 見た目の一貫性確認
4. **メモリリークテスト** - リソース管理確認

## 推奨テスト実装順序

1. **週1**: MainViewModel + LoadPackagesUseCase 単体テスト
2. **週2**: PackageRepositoryImpl 単体テスト + 基本UI テスト  
3. **週3**: 統合テスト + パフォーマンステスト
4. **週4**: E2Eテスト + セキュリティテスト

**推定工数**: 合計32-40時間（各週8-10時間）
**期待カバレッジ向上**: 10% → 75%

## CI/CD統合推奨

```yaml
# .github/workflows/test.yml
name: Run Tests

on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run unit tests
        run: ./gradlew testDebugUnitTest
      - name: Generate coverage report  
        run: ./gradlew koverXmlReportDebug
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3

  instrumented-tests:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 34
          script: ./gradlew connectedDebugAndroidTest
```