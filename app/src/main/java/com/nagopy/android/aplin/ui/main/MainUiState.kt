package com.nagopy.android.aplin.ui.main

import androidx.annotation.StringRes
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackagesModel

data class MainUiState(
    val isLoading: Boolean,
    val packagesModel: PackagesModel? = null,
)

enum class AppCategory(@StringRes val labelId: Int) {
    USERS(R.string.category_users),
    DISABLEABLE(R.string.category_disableable),
    ALL(R.string.category_users),
}
