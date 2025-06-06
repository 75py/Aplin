package com.nagopy.android.aplin

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatcherModule =
    module {
        single(named(CoroutineDispatcherType.IO)) { Dispatchers.IO }
    }

enum class CoroutineDispatcherType {
    IO,
}
