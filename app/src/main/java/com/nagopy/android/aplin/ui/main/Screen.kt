package com.nagopy.android.aplin.ui.main

import androidx.annotation.StringRes
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Top : Screen("main", R.string.app_name)

    object Preferences : Screen("pref", R.string.preferences)

    object UserAppList : AppListScreen("userAppList", R.string.category_users) {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.User.getAppList(packagesModel, searchText)
    }

    object DisableableAppList :
        AppListScreen("disableableAppList", R.string.category_disableable) {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.Disableable.getAppList(packagesModel, searchText)
    }

    object DisabledAppList :
        AppListScreen("disabledAppList", R.string.category_disabled) {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.Disabled.getAppList(packagesModel, searchText)
    }

    object AllAppList : AppListScreen("allAppList", R.string.category_all) {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.All.getAppList(packagesModel, searchText)
    }

    abstract class AppListScreen(route: String, @StringRes resourceId: Int) :
        Screen(route, resourceId) {
        abstract fun getAppList(
            packagesModel: PackagesModel,
            searchText: String
        ): List<PackageModel>
    }

    companion object {
        val appListScreens =
            listOf(UserAppList, DisableableAppList, DisabledAppList, AllAppList)

        private val values = listOf(
            Top,
            Preferences
        ) + appListScreens

        fun find(route: String?): Screen {
            return values.find { it.route == route } ?: Top
        }
    }
}
