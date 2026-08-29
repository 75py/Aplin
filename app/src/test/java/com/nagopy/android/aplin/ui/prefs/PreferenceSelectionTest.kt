package com.nagopy.android.aplin.ui.prefs

import org.junit.Assert.assertEquals
import org.junit.Test

class PreferenceSelectionTest {
    @Test
    fun toggleDisplayItem_addsUnselectedItem() {
        val selectedItems = setOf(DisplayItem.FirstInstallTime)

        val result = toggleDisplayItem(selectedItems, DisplayItem.VersionName)

        assertEquals(setOf(DisplayItem.FirstInstallTime, DisplayItem.VersionName), result)
    }

    @Test
    fun toggleDisplayItem_removesSelectedItem() {
        val selectedItems = setOf(DisplayItem.FirstInstallTime, DisplayItem.VersionName)

        val result = toggleDisplayItem(selectedItems, DisplayItem.FirstInstallTime)

        assertEquals(setOf(DisplayItem.VersionName), result)
    }
}
