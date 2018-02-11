package com.nagopy.android.aplin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class AppListViewModel() : ViewModel() {


    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val appListViewModel = AppListViewModel()
            return appListViewModel as T
        }
    }

}
