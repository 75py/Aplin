package com.nagopy.android.aplin.domain.usecase

import org.koin.dsl.module

val useCaseModule = module {
    single { LoadPackagesUseCase(get(), get()) }

    single { CategorizePackageUseCase(get(), get()) }
}
