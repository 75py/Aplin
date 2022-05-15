package com.nagopy.android.aplin.domain.usecase

import android.content.pm.PackageInfo
import com.nagopy.android.aplin.data.repository.PackageRepository
import com.nagopy.android.aplin.domain.model.PackageModel
import com.nagopy.android.aplin.domain.model.PackagesModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class LoadPackagesUseCase(
    private val packageRepository: PackageRepository,
    private val categorizePackageUseCase: CategorizePackageUseCase,
) {

    suspend fun execute(): PackagesModel = coroutineScope {
        withContext(coroutineContext) {
            val loadAllAsync = async { packageRepository.loadAll() }
            val loadHomePackageNamesAsync = async { packageRepository.loadHomePackageNames() }
            val loadCurrentDefaultHomePackageNameAsync =
                async { packageRepository.loadCurrentDefaultHomePackageName() }

            val src = loadAllAsync.await()
            val homePackages = loadHomePackageNamesAsync.await()
            val currentDefaultHomePackageName = loadCurrentDefaultHomePackageNameAsync.await()
            val disableable = src
                .filter {
                    categorizePackageUseCase.isDisableable(
                        it,
                        homePackages,
                        currentDefaultHomePackageName
                    )
                }
                .map { it.toPackageModel() }
                .sortedWith(compareBy({ it.label }, { it.packageName }))
            val disabled = src
                .filter { !it.applicationInfo.enabled }
                .map { it.toPackageModel() }
                .sortedWith(compareBy({ it.label }, { it.packageName }))
            val users = src
                .filter { !categorizePackageUseCase.isBundled(it) }
                .map { it.toPackageModel() }
                .sortedWith(compareBy({ it.label }, { it.packageName }))
            val all = src
                .map { it.toPackageModel() }
                .sortedWith(compareBy({ it.label }, { it.packageName }))

            PackagesModel(
                disableablePackages = disableable,
                disabledPackages = disabled,
                userPackages = users,
                allPackages = all,
            )
        }
    }

    private fun PackageInfo.toPackageModel(): PackageModel {
        return PackageModel(
            packageName,
            packageRepository.loadLabel(applicationInfo),
            packageRepository.loadIcon(applicationInfo),
            applicationInfo.enabled,
            firstInstallTime,
            lastUpdateTime,
            packageName,
        )
    }
}
