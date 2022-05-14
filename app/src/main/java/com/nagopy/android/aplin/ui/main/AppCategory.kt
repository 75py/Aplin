package com.nagopy.android.aplin.ui.main

import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel

sealed class AppCategory {
    abstract fun getAppList(packagesModel: PackagesModel, searchText: String): List<PackageModel>

    object User : AppCategory() {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            packagesModel.userPackages.filter(searchText)
    }

    object Disableable : AppCategory() {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            packagesModel.disableablePackages.filter(searchText)
    }

    object All : AppCategory() {
        override fun getAppList(packagesModel: PackagesModel, searchText: String) =
            packagesModel.allPackages.filter(searchText)
    }

    protected fun List<PackageModel>.filter(searchText: String): List<PackageModel> {
        if (searchText.isEmpty()) {
            return this
        }
        return this.filter {
            it.label.contains(searchText) || it.packageName.contains(
                searchText
            )
        }
    }
}
