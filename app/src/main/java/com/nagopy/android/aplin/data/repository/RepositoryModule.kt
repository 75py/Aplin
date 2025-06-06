package com.nagopy.android.aplin.data.repository

import org.koin.dsl.module

val repositoryModule =
    module {

        single<PackageRepository> { PackageRepositoryImpl(get()) }

        single<DevicePolicyRepository> { DevicePolicyRepositoryImpl(get()) }
    }
