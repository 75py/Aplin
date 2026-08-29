package com.nagopy.android.aplin.ui.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class UserDataStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var dataStoreScope: CoroutineScope
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var userDataStore: UserDataStore

    @Before
    fun setUp() {
        dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        dataStore =
            PreferenceDataStoreFactory.create(scope = dataStoreScope) {
                File(temporaryFolder.root, "settings.preferences_pb")
            }
        userDataStore = UserDataStore(dataStore)
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun defaults_matchExistingSettingsBehavior() =
        runBlocking {
            assertEquals(emptyList<DisplayItem>(), userDataStore.displayItems.first())
            assertEquals(SortOrder.DEFAULT, userDataStore.sortOrder.first())
        }

    @Test
    fun setDisplayItems_storesEnumNamesInExistingStringSet() =
        runBlocking {
            val selectedItems = setOf(DisplayItem.FirstInstallTime, DisplayItem.VersionName)

            userDataStore.setDisplayItems(selectedItems)

            val preferences = dataStore.data.first()
            assertEquals(
                setOf("FirstInstallTime", "VersionName"),
                preferences[stringSetPreferencesKey(DisplayItem.KEY)],
            )
            assertEquals(selectedItems, userDataStore.displayItems.first().toSet())
        }

    @Test
    fun setSortOrder_storesEnumNameInExistingStringPreference() =
        runBlocking {
            userDataStore.setSortOrder(SortOrder.LastUpdateTimeDesc)

            val preferences = dataStore.data.first()
            assertEquals(
                "LastUpdateTimeDesc",
                preferences[stringPreferencesKey(SortOrder.KEY)],
            )
            assertEquals(SortOrder.LastUpdateTimeDesc, userDataStore.sortOrder.first())
        }

    @Test
    fun unknownStoredValues_keepExistingFallbackBehavior() =
        runBlocking {
            dataStore.edit { preferences ->
                preferences[stringSetPreferencesKey(DisplayItem.KEY)] =
                    setOf(DisplayItem.VersionName.name, "RemovedDisplayItem")
                preferences[stringPreferencesKey(SortOrder.KEY)] = "RemovedSortOrder"
            }

            assertEquals(listOf(DisplayItem.VersionName), userDataStore.displayItems.first())
            assertEquals(SortOrder.DEFAULT, userDataStore.sortOrder.first())
        }
}
