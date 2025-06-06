package com.nagopy.android.aplin.ui.prefs

import android.graphics.drawable.Drawable
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class SortOrderTest {

    private val mockIcon: Drawable = mockk()

    private fun createPackageModel(
        packageName: String,
        label: String,
        firstInstallTime: Long = 0L,
        lastUpdateTime: Long = 0L
    ): PackageModel {
        return PackageModel(
            packageName = packageName,
            label = label,
            icon = mockIcon,
            isEnabled = true,
            firstInstallTime = firstInstallTime,
            lastUpdateTime = lastUpdateTime,
            versionName = "1.0"
        )
    }

    @Test
    fun appName_sortsPackagesByLabelThenPackageName() {
        val packages = listOf(
            createPackageModel("com.example.z", "App B"),
            createPackageModel("com.example.a", "App A"),
            createPackageModel("com.example.b", "App A") // Same label, different package name
        )

        val sorted = SortOrder.AppName.sort(packages)

        assertEquals("com.example.a", sorted[0].packageName)
        assertEquals("com.example.b", sorted[1].packageName)
        assertEquals("com.example.z", sorted[2].packageName)
    }

    @Test
    fun appPackageName_sortsPackagesByPackageName() {
        val packages = listOf(
            createPackageModel("com.example.z", "App C"),
            createPackageModel("com.example.a", "App B"),
            createPackageModel("com.example.b", "App A")
        )

        val sorted = SortOrder.AppPackageName.sort(packages)

        assertEquals("com.example.a", sorted[0].packageName)
        assertEquals("com.example.b", sorted[1].packageName)
        assertEquals("com.example.z", sorted[2].packageName)
    }

    @Test
    fun firstInstallTimeDesc_sortsPackagesByFirstInstallTimeDescending() {
        val packages = listOf(
            createPackageModel("com.example.old", "App Old", firstInstallTime = 1000L),
            createPackageModel("com.example.new", "App New", firstInstallTime = 3000L),
            createPackageModel("com.example.mid", "App Mid", firstInstallTime = 2000L)
        )

        val sorted = SortOrder.FirstInstallTimeDesc.sort(packages)

        assertEquals("com.example.new", sorted[0].packageName) // 3000L first
        assertEquals("com.example.mid", sorted[1].packageName) // 2000L second
        assertEquals("com.example.old", sorted[2].packageName) // 1000L last
    }

    @Test
    fun lastUpdateTimeDesc_sortsPackagesByLastUpdateTimeDescending() {
        val packages = listOf(
            createPackageModel("com.example.old", "App Old", lastUpdateTime = 1000L),
            createPackageModel("com.example.new", "App New", lastUpdateTime = 3000L),
            createPackageModel("com.example.mid", "App Mid", lastUpdateTime = 2000L)
        )

        val sorted = SortOrder.LastUpdateTimeDesc.sort(packages)

        assertEquals("com.example.new", sorted[0].packageName) // 3000L first
        assertEquals("com.example.mid", sorted[1].packageName) // 2000L second
        assertEquals("com.example.old", sorted[2].packageName) // 1000L last
    }

    @Test
    fun firstInstallTimeDesc_withSameTime_sortsSecondaryByLabelThenPackageName() {
        val packages = listOf(
            createPackageModel("com.example.z", "App B", firstInstallTime = 1000L),
            createPackageModel("com.example.a", "App A", firstInstallTime = 1000L),
            createPackageModel("com.example.b", "App A", firstInstallTime = 1000L)
        )

        val sorted = SortOrder.FirstInstallTimeDesc.sort(packages)

        assertEquals("com.example.a", sorted[0].packageName)
        assertEquals("com.example.b", sorted[1].packageName)
        assertEquals("com.example.z", sorted[2].packageName)
    }

    @Test
    fun lastUpdateTimeDesc_withSameTime_sortsSecondaryByLabelThenPackageName() {
        val packages = listOf(
            createPackageModel("com.example.z", "App B", lastUpdateTime = 1000L),
            createPackageModel("com.example.a", "App A", lastUpdateTime = 1000L),
            createPackageModel("com.example.b", "App A", lastUpdateTime = 1000L)
        )

        val sorted = SortOrder.LastUpdateTimeDesc.sort(packages)

        assertEquals("com.example.a", sorted[0].packageName)
        assertEquals("com.example.b", sorted[1].packageName)
        assertEquals("com.example.z", sorted[2].packageName)
    }

    @Test
    fun sortOrder_withEmptyList_returnsEmptyList() {
        val emptyList = emptyList<PackageModel>()

        assertEquals(emptyList, SortOrder.AppName.sort(emptyList))
        assertEquals(emptyList, SortOrder.AppPackageName.sort(emptyList))
        assertEquals(emptyList, SortOrder.FirstInstallTimeDesc.sort(emptyList))
        assertEquals(emptyList, SortOrder.LastUpdateTimeDesc.sort(emptyList))
    }

    @Test
    fun sortOrder_withSingleItem_returnsSameList() {
        val singleItem = listOf(createPackageModel("com.example.single", "Single App"))

        assertEquals(singleItem, SortOrder.AppName.sort(singleItem))
        assertEquals(singleItem, SortOrder.AppPackageName.sort(singleItem))
        assertEquals(singleItem, SortOrder.FirstInstallTimeDesc.sort(singleItem))
        assertEquals(singleItem, SortOrder.LastUpdateTimeDesc.sort(singleItem))
    }

    @Test
    fun sortPackagesModel_appliesSortingToAllLists() {
        val packages = listOf(
            createPackageModel("com.example.z", "App B"),
            createPackageModel("com.example.a", "App A")
        )

        val packagesModel = PackagesModel(
            disableablePackages = packages,
            disabledPackages = packages,
            userPackages = packages,
            allPackages = packages
        )

        val sorted = SortOrder.AppName.sort(packagesModel)

        // All lists should be sorted by app name
        assertEquals("com.example.a", sorted.disableablePackages[0].packageName)
        assertEquals("com.example.a", sorted.disabledPackages[0].packageName)
        assertEquals("com.example.a", sorted.userPackages[0].packageName)
        assertEquals("com.example.a", sorted.allPackages[0].packageName)
    }

    @Test
    fun sortOrderConstants_haveExpectedValues() {
        assertEquals("sort_order", SortOrder.KEY)
        assertEquals(SortOrder.AppName, SortOrder.DEFAULT)
    }
}
