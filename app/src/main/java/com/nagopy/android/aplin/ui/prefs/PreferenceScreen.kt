package com.nagopy.android.aplin.ui.prefs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.ui.theme.AplinTheme
import kotlinx.coroutines.launch

@Composable
fun PreferenceScreen() {
    val context = LocalContext.current
    val userDataStore = remember(context) { UserDataStore(context.dataStore) }
    val coroutineScope = rememberCoroutineScope()
    val displayItems by userDataStore.displayItems.collectAsState(initial = emptyList())
    val sortOrder by userDataStore.sortOrder.collectAsState(initial = SortOrder.DEFAULT)

    PreferenceScreenContent(
        displayItems = displayItems.toSet(),
        sortOrder = sortOrder,
        onDisplayItemsChanged = { selectedItems ->
            coroutineScope.launch {
                userDataStore.setDisplayItems(selectedItems)
            }
        },
        onSortOrderChanged = { selectedSortOrder ->
            coroutineScope.launch {
                userDataStore.setSortOrder(selectedSortOrder)
            }
        },
    )
}

@Composable
private fun PreferenceScreenContent(
    displayItems: Set<DisplayItem>,
    sortOrder: SortOrder,
    onDisplayItemsChanged: (Set<DisplayItem>) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
) {
    val displayItemLabels = DisplayItem.values().associateWith { stringResource(id = it.labelResId) }
    val sortOrderLabels = SortOrder.values().associateWith { stringResource(id = it.labelResId) }
    val noItemsSelected = stringResource(id = R.string.pref_no_items_selected)
    val displayItemsSummary =
        DisplayItem
            .values()
            .filter(displayItems::contains)
            .joinToString { displayItemLabels.getValue(it) }
            .ifEmpty { noItemsSelected }
    val sortOrderSummary = sortOrderLabels.getValue(sortOrder)

    var pendingDisplayItems by remember { mutableStateOf<Set<DisplayItem>?>(null) }
    var pendingSortOrder by remember { mutableStateOf<SortOrder?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        PreferenceRow(
            title = stringResource(id = R.string.display_items),
            summary = displayItemsSummary,
            onClick = { pendingDisplayItems = displayItems },
        )
        Divider()
        PreferenceRow(
            title = stringResource(id = R.string.pref_sort_order),
            summary = sortOrderSummary,
            onClick = { pendingSortOrder = sortOrder },
        )
        Divider()
    }

    pendingDisplayItems?.let { selectedItems ->
        DisplayItemsDialog(
            selectedItems = selectedItems,
            labels = displayItemLabels,
            onSelectionChanged = { pendingDisplayItems = it },
            onConfirm = {
                onDisplayItemsChanged(selectedItems)
                pendingDisplayItems = null
            },
            onDismiss = { pendingDisplayItems = null },
        )
    }

    pendingSortOrder?.let { selectedSortOrder ->
        SortOrderDialog(
            selectedSortOrder = selectedSortOrder,
            labels = sortOrderLabels,
            onSelectionChanged = { pendingSortOrder = it },
            onConfirm = {
                onSortOrderChanged(selectedSortOrder)
                pendingSortOrder = null
            },
            onDismiss = { pendingSortOrder = null },
        )
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    summary: String,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .semantics(mergeDescendants = true) {
                    stateDescription = summary
                }.padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
        )
        Text(
            text = summary,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
private fun DisplayItemsDialog(
    selectedItems: Set<DisplayItem>,
    labels: Map<DisplayItem, String>,
    onSelectionChanged: (Set<DisplayItem>) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.display_items)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                DisplayItem.values().forEach { item ->
                    val selected = item in selectedItems
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = selected,
                                    role = Role.Checkbox,
                                    onValueChange = {
                                        onSelectionChanged(toggleDisplayItem(selectedItems, item))
                                    },
                                ).padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = selected,
                            onCheckedChange = null,
                            modifier = Modifier.clearAndSetSemantics { },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = labels.getValue(item))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}

@Composable
private fun SortOrderDialog(
    selectedSortOrder: SortOrder,
    labels: Map<SortOrder, String>,
    onSelectionChanged: (SortOrder) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.pref_sort_order)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                SortOrder.values().forEach { sortOrder ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = sortOrder == selectedSortOrder,
                                    role = Role.RadioButton,
                                    onClick = { onSelectionChanged(sortOrder) },
                                ).padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = sortOrder == selectedSortOrder,
                            onClick = null,
                            modifier = Modifier.clearAndSetSemantics { },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = labels.getValue(sortOrder))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}

internal fun toggleDisplayItem(
    selectedItems: Set<DisplayItem>,
    item: DisplayItem,
): Set<DisplayItem> =
    if (item in selectedItems) {
        selectedItems - item
    } else {
        selectedItems + item
    }

@Preview(showBackground = true)
@Composable
fun PreferenceScreenPreview() {
    AplinTheme {
        PreferenceScreenContent(
            displayItems = setOf(DisplayItem.FirstInstallTime, DisplayItem.VersionName),
            sortOrder = SortOrder.DEFAULT,
            onDisplayItemsChanged = {},
            onSortOrderChanged = {},
        )
    }
}
