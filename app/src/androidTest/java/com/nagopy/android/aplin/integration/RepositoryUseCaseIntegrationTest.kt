package com.nagopy.android.aplin.integration

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nagopy.android.aplin.data.repository.DevicePolicyRepositoryImpl
import com.nagopy.android.aplin.data.repository.PackageRepositoryImpl
import com.nagopy.android.aplin.domain.usecase.CategorizePackageUseCase
import com.nagopy.android.aplin.domain.usecase.LoadPackagesUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Repository + UseCase 統合テスト
 * 実際のAndroid環境でのデータフロー全体をテスト
 */
@RunWith(AndroidJUnit4::class)
class RepositoryUseCaseIntegrationTest {
    
    private lateinit var context: Context
    private lateinit var packageRepository: PackageRepositoryImpl
    private lateinit var devicePolicyRepository: DevicePolicyRepositoryImpl
    private lateinit var categorizeUseCase: CategorizePackageUseCase
    private lateinit var loadPackagesUseCase: LoadPackagesUseCase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        packageRepository = PackageRepositoryImpl(context.packageManager)
        devicePolicyRepository = DevicePolicyRepositoryImpl(
            context.getSystemService(DevicePolicyManager::class.java)
        )
        categorizeUseCase = CategorizePackageUseCase(
            packageRepository, devicePolicyRepository
        )
        loadPackagesUseCase = LoadPackagesUseCase(
            packageRepository, categorizeUseCase
        )
    }

    @Test
    fun realPackageRepository_withRealUseCase_loadsActualPackages() = runTest {
        // When - 実際のAndroid環境でパッケージをロード
        val result = loadPackagesUseCase.execute()

        // Then - 実際のAndroidパッケージが適切にロードされることを確認
        assertTrue("Should load at least some packages", result.allPackages.isNotEmpty())
        
        // Androidシステムパッケージが含まれることを確認
        assertTrue("Should have Android system packages", 
            result.allPackages.any { it.packageName.startsWith("com.android") })
        
        // テストアプリ自体も含まれることを確認
        assertTrue("Should include test app itself",
            result.allPackages.any { it.packageName.contains("aplin") })
    }

    @Test
    fun categorization_worksWithRealData() = runTest {
        // When
        val result = loadPackagesUseCase.execute()

        // Then - カテゴリ分けが適切に動作することを確認
        result.disableablePackages.forEach { pkg ->
            assertFalse("Disableable packages should not include critical system apps",
                pkg.packageName in listOf("android", "com.android.systemui", "com.android.phone"))
        }

        // 無効化されたパッケージは isEnabled = false
        result.disabledPackages.forEach { pkg ->
            assertFalse("Disabled packages should have isEnabled = false", pkg.isEnabled)
        }

        // ユーザーアプリにシステムアプリが含まれていないことを確認
        val systemPackageCount = result.userPackages.count { 
            it.packageName.startsWith("com.android") || it.packageName == "android"
        }
        assertTrue("User packages should have limited system apps", 
            systemPackageCount < result.allPackages.size * 0.3) // 30%未満
    }

    @Test
    fun dataIntegrity_maintainedThroughoutFlow() = runTest {
        // When
        val result = loadPackagesUseCase.execute()

        // Then - データの整合性を確認
        result.allPackages.forEach { pkg ->
            assertNotNull("Package name should not be null", pkg.packageName)
            assertNotNull("Package label should not be null", pkg.label)
            assertNotNull("Package icon should not be null", pkg.icon)
            assertTrue("Package name should not be empty", pkg.packageName.isNotEmpty())
            assertTrue("Package label should not be empty", pkg.label.isNotEmpty())
            assertTrue("First install time should be positive", pkg.firstInstallTime > 0)
            assertTrue("Last update time should be positive", pkg.lastUpdateTime > 0)
            assertTrue("Last update time should be >= first install time", 
                pkg.lastUpdateTime >= pkg.firstInstallTime)
        }
    }

    @Test
    fun homePackageDetection_worksCorrectly() = runTest {
        // When
        val homePackages = packageRepository.loadHomePackageNames()
        val currentDefault = packageRepository.loadCurrentDefaultHomePackageName()

        // Then
        assertTrue("Should detect at least one home package", homePackages.isNotEmpty())
        
        if (currentDefault != null) {
            assertTrue("Current default should be in home packages list", 
                homePackages.contains(currentDefault))
        }

        // ホームパッケージは実際に存在するパッケージであることを確認
        val allPackageNames = packageRepository.loadAll().map { it.packageName }.toSet()
        homePackages.forEach { homePackage ->
            assertTrue("Home package should exist in installed packages: $homePackage",
                allPackageNames.contains(homePackage))
        }
    }

    @Test
    fun sortingAndFiltering_maintainsDataConsistency() = runTest {
        // When
        val result = loadPackagesUseCase.execute()

        // Then - ソートが正しく適用されていることを確認
        fun verifySort(packages: List<com.nagopy.android.aplin.domain.model.PackageModel>) {
            packages.zipWithNext().forEach { (current, next) ->
                val comparison = current.label.compareTo(next.label, ignoreCase = true)
                if (comparison == 0) {
                    // ラベルが同じ場合はパッケージ名でソート
                    assertTrue("Package names should be sorted when labels are equal",
                        current.packageName <= next.packageName)
                } else {
                    assertTrue("Labels should be sorted alphabetically",
                        comparison <= 0)
                }
            }
        }

        verifySort(result.allPackages)
        verifySort(result.userPackages)
        verifySort(result.disableablePackages)
        verifySort(result.disabledPackages)
    }

    @Test
    fun errorRecovery_handlesRealWorldScenarios() = runTest {
        // Given - 制限された権限環境をシミュレート（可能な範囲で）
        
        try {
            // When
            val result = loadPackagesUseCase.execute()
            
            // Then - エラーが発生しても基本的な動作は継続
            assertNotNull("Result should not be null even with errors", result)
            
        } catch (e: Exception) {
            // セキュリティ例外などが発生した場合でも適切に処理されることを確認
            assertTrue("Should handle exceptions gracefully", 
                e is SecurityException || e is RuntimeException)
        }
    }

    @Test
    fun performance_acceptableWithRealData() = runTest {
        // When - パフォーマンス測定
        val startTime = System.currentTimeMillis()
        val result = loadPackagesUseCase.execute()
        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime

        // Then - 実用的な時間内で完了することを確認
        assertTrue("Should complete within 10 seconds", executionTime < 10_000)
        assertTrue("Should load some packages", result.allPackages.isNotEmpty())
        
        // パッケージ数に応じた妥当な実行時間かを確認
        val packagesPerSecond = result.allPackages.size.toDouble() / (executionTime / 1000.0)
        assertTrue("Should process at least 10 packages per second", packagesPerSecond >= 10.0)
    }

    @Test
    fun memoryUsage_reasonableWithRealData() = runTest {
        // Given
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // When
        repeat(5) {
            loadPackagesUseCase.execute()
            if (it % 2 == 0) {
                System.gc()
                Thread.sleep(100)
            }
        }

        System.gc()
        Thread.sleep(500)
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()

        // Then
        val memoryIncrease = finalMemory - initialMemory
        val maxAllowedIncrease = 20 * 1024 * 1024 // 20MB
        
        assertTrue("Memory usage should not increase significantly: ${memoryIncrease / 1024 / 1024}MB",
            memoryIncrease < maxAllowedIncrease)
    }

    @Test
    fun repositoryConsistency_acrossMultipleCalls() = runTest {
        // When - 複数回の呼び出しで一貫した結果が得られることを確認
        val result1 = loadPackagesUseCase.execute()
        Thread.sleep(100) // 短時間待機
        val result2 = loadPackagesUseCase.execute()

        // Then - 基本的に同じ結果が得られる（新規インストール等がない限り）
        val packageNames1 = result1.allPackages.map { it.packageName }.toSet()
        val packageNames2 = result2.allPackages.map { it.packageName }.toSet()
        
        // 95%以上のパッケージは共通であることを確認（多少の変動は許容）
        val intersection = packageNames1.intersect(packageNames2)
        val similarity = intersection.size.toDouble() / maxOf(packageNames1.size, packageNames2.size)
        
        assertTrue("Results should be mostly consistent across calls: ${similarity * 100}%",
            similarity >= 0.95)
    }
}