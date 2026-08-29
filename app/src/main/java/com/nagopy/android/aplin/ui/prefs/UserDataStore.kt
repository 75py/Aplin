package com.nagopy.android.aplin.ui.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(private val dataStore: DataStore<Preferences>) {
    private val displayItemsKey = stringSetPreferencesKey(DisplayItem.KEY)
    private val sortOrderKey = stringPreferencesKey(SortOrder.KEY)

    val displayItems: Flow<List<DisplayItem>> =
        dataStore.data.map { it[displayItemsKey] ?: emptySet() }.map {
            it.mapNotNull { v ->
                DisplayItem.values().firstOrNull { item -> item.name == v }
            }
        }

    val sortOrder: Flow<SortOrder> =
        dataStore.data.map { it[sortOrderKey] ?: SortOrder.DEFAULT.name }.map {
            SortOrder.values().firstOrNull { item -> item.name == it } ?: SortOrder.DEFAULT
        }

    suspend fun setDisplayItems(displayItems: Set<DisplayItem>) {
        dataStore.edit { preferences ->
            preferences[displayItemsKey] = displayItems.mapTo(mutableSetOf()) { it.name }
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[sortOrderKey] = sortOrder.name
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
