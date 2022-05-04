package com.nagopy.android.aplin.data.repository

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable

interface PackageRepository {
    suspend fun loadAll(): List<PackageInfo>

    suspend fun loadHomePackageNames(): Set<String>

    suspend fun loadCurrentDefaultHomePackageName(): String?

    fun loadLabel(applicationInfo: ApplicationInfo): String

    fun loadIcon(applicationInfo: ApplicationInfo): Drawable

    val systemPackage: PackageInfo?

    val permissionControllerPackageName: String?

    val servicesSystemSharedLibraryPackageName: String?

    val sharedSystemSharedLibraryPackageName: String?

    val printSpoolerPackageName: String?

    val deviceProvisioningPackage: String?
}
