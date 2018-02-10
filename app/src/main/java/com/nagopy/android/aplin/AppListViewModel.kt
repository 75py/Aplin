package com.nagopy.android.aplin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.graphics.drawable.Drawable
import com.nagopy.android.aplin.loader.IconLoader

class AppListViewModel(val iconLoader: IconLoader) : ViewModel() {

    fun loadIcon(packageName: String): Drawable {
        return iconLoader.loadIcon(packageName)
    }

    class Factory(val iconLoader: IconLoader) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val appListViewModel = AppListViewModel(iconLoader)
            return appListViewModel as T
        }
    }

}
