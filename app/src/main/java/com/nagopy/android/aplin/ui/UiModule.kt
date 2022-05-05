package com.nagopy.android.aplin.ui

import com.nagopy.android.aplin.CoroutineDispatcherType
import com.nagopy.android.aplin.ui.ads.AdsViewModel
import com.nagopy.android.aplin.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val uiModule = module {
    viewModel { MainViewModel(get(), get(), get(), get(named(CoroutineDispatcherType.IO))) }
    viewModel { AdsViewModel(get()) }
}
