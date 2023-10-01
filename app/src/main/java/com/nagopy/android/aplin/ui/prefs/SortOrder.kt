package com.nagopy.android.aplin.ui.prefs

import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel

enum class SortOrder(val labelResId: Int) {

    AppName(R.string.pref_sort_order_app_name) {
        override fun sort(packages: List<PackageModel>): List<PackageModel> {
            return packages.sortedWith(compareBy({ it.label }, { it.packageName }))
        }
    },
    AppPackageName(R.string.pref_sort_order_app_package_name) {
        override fun sort(packages: List<PackageModel>): List<PackageModel> {
            return packages.sortedWith(compareBy { it.packageName })
        }
    },
    FirstInstallTimeDesc(R.string.pref_sort_order_first_install_time_desc) {
        override fun sort(packages: List<PackageModel>): List<PackageModel> {
            return packages.sortedWith(
                compareBy(
                    { it.firstInstallTime * -1 },
                    { it.label },
                    { it.packageName }
                )
            )
        }
    },
    LastUpdateTimeDesc(R.string.pref_sort_order_last_update_time_desc) {
        override fun sort(packages: List<PackageModel>): List<PackageModel> {
            return packages.sortedWith(
                compareBy(
                    { it.lastUpdateTime * -1 },
                    { it.label },
                    { it.packageName }
                )
            )
        }
    }
    ;

    protected abstract fun sort(packages: List<PackageModel>): List<PackageModel>

    fun sort(packagesModel: PackagesModel): PackagesModel {
        return PackagesModel(
            disableablePackages = sort(packagesModel.disableablePackages),
            disabledPackages = sort(packagesModel.disabledPackages),
            userPackages = sort(packagesModel.userPackages),
            allPackages = sort(packagesModel.allPackages)
        )
    }

    companion object {
        const val KEY = "sort_order"
        val DEFAULT = AppName
    }
}
