package com.nagopy.android.aplin.ui.prefs

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jamal.composeprefs.ui.PrefsScreen
import com.jamal.composeprefs.ui.prefs.ListPref
import com.jamal.composeprefs.ui.prefs.MultiSelectListPref
import com.nagopy.android.aplin.R

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun PreferenceScreen() {
    PrefsScreen(dataStore = LocalContext.current.dataStore) {
        prefsItem {
            MultiSelectListPref(
                key = DisplayItem.KEY,
                title = stringResource(id = R.string.display_items),
                entries = DisplayItem.values().associate {
                    it.name to stringResource(id = it.labelResId)
                }
            )
        }
        prefsItem {
            ListPref(
                key = SortOrder.KEY,
                title = stringResource(id = R.string.pref_sort_order),
                entries = SortOrder.values().associate {
                    it.name to stringResource(id = it.labelResId)
                },
                defaultValue = SortOrder.DEFAULT.name
            )
        }
    }
}

@Preview
@Composable
fun PreferenceScreenPreview() {
    PreferenceScreen()
}
