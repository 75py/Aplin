package com.nagopy.android.aplin.ui.main

import com.nagopy.android.aplin.domain.model.PackagesModel

data class MainUiState(
    val isLoading: Boolean,
    val packagesModel: PackagesModel? = null,
)

enum class AppCategory {
    USERS,
    DISABLEABLE,
    ALL,
}