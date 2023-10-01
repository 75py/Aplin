package com.nagopy.android.aplin.ui.main

import com.nagopy.android.aplin.domain.model.PackagesModel
import com.nagopy.android.aplin.ui.prefs.SortOrder

data class MainUiState(
    val isLoading: Boolean,
    val packagesModel: PackagesModel? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
    val searchText: String = ""
)
