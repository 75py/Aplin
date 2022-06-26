package com.nagopy.android.aplin.ui.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(dataStore: DataStore<Preferences>) {

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
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
