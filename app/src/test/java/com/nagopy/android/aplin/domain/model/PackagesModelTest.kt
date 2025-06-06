package com.nagopy.android.aplin.domain.model

import android.graphics.drawable.Drawable
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PackagesModelTest {

    private val mockIcon: Drawable = mockk()

    private fun createPackageModel(packageName: String): PackageModel {
        return PackageModel(
            packageName = packageName,
            label = "App $packageName",
            icon = mockIcon,
            isEnabled = true,
            firstInstallTime = 0L,
            lastUpdateTime = 0L,
            versionName = "1.0"
        )
    }

    @Test
    fun packagesModel_hasCorrectProperties() {
        val disableablePackages = listOf(createPackageModel("disableable"))
        val disabledPackages = listOf(createPackageModel("disabled"))
        val userPackages = listOf(createPackageModel("user"))
        val allPackages = listOf(createPackageModel("all"))

        val packagesModel = PackagesModel(
            disableablePackages = disableablePackages,
            disabledPackages = disabledPackages,
            userPackages = userPackages,
            allPackages = allPackages
        )

        assertEquals(disableablePackages, packagesModel.disableablePackages)
        assertEquals(disabledPackages, packagesModel.disabledPackages)
        assertEquals(userPackages, packagesModel.userPackages)
        assertEquals(allPackages, packagesModel.allPackages)
    }

    @Test
    fun packagesModel_withEmptyLists_handlesCorrectly() {
        val packagesModel = PackagesModel(
            disableablePackages = emptyList(),
            disabledPackages = emptyList(),
            userPackages = emptyList(),
            allPackages = emptyList()
        )

        assertTrue(packagesModel.disableablePackages.isEmpty())
        assertTrue(packagesModel.disabledPackages.isEmpty())
        assertTrue(packagesModel.userPackages.isEmpty())
        assertTrue(packagesModel.allPackages.isEmpty())
    }

    @Test
    fun packagesModel_withMultiplePackages_maintainsOrder() {
        val packages = listOf(
            createPackageModel("first"),
            createPackageModel("second"),
            createPackageModel("third")
        )

        val packagesModel = PackagesModel(
            disableablePackages = packages,
            disabledPackages = packages.reversed(),
            userPackages = packages,
            allPackages = packages.reversed()
        )

        assertEquals("first", packagesModel.disableablePackages[0].packageName)
        assertEquals("second", packagesModel.disableablePackages[1].packageName)
        assertEquals("third", packagesModel.disableablePackages[2].packageName)

        // Check reversed order is maintained
        assertEquals("third", packagesModel.disabledPackages[0].packageName)
        assertEquals("second", packagesModel.disabledPackages[1].packageName)
        assertEquals("first", packagesModel.disabledPackages[2].packageName)
    }
}
