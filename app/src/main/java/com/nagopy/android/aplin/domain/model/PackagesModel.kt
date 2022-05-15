package com.nagopy.android.aplin.domain.model

data class PackagesModel(
    val disableablePackages: List<PackageModel>,
    val disabledPackages: List<PackageModel>,
    val userPackages: List<PackageModel>,
    val allPackages: List<PackageModel>,
)
