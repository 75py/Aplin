package com.nagopy.android.aplin.ui.main

import androidx.annotation.StringRes
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Top : Screen("main", R.string.app_name)

    object UserAppList : Screen("userAppList", R.string.category_users), AppList {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.User.getAppList(packagesModel, searchText)
    }

    object DisableableAppList :
        Screen("disableableAppList", R.string.category_disableable),
        AppList {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.Disableable.getAppList(packagesModel, searchText)
    }

    object AllAppList : Screen("allAppList", R.string.category_all), AppList {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            AppCategory.All.getAppList(packagesModel, searchText)
    }

    interface AppList {
        fun getAppList(packagesModel: PackagesModel, searchText: String): List<PackageModel>
    }

    companion object {
        fun find(route: String?): Screen {
            return when (route) {
                UserAppList.route -> UserAppList
                DisableableAppList.route -> DisableableAppList
                AllAppList.route -> AllAppList
                else -> Top
            }
        }
    }
}
