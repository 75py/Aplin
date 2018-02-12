package com.nagopy.android.aplin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import com.nagopy.android.aplin.loader.AppInfo
import com.nagopy.android.aplin.loader.AppLoader
import timber.log.Timber

class MainViewModel(private val appLoader: AppLoader) : ViewModel() {

    val isLoaded = ObservableBoolean(false)

    private var loadedAppList: List<AppInfo>? = null
    private var onSearchTextListeners = ArrayList<OnSearchTextListener>()

    suspend fun loadApplications() {
        synchronized(this, {
            if (loadedAppList != null) {
                return
            }

            val appList = appLoader.load()

            if (BuildConfig.DEBUG) {
                appList.forEach {
                    Timber.v("%s", it)
                }
            }

            loadedAppList = appList
            isLoaded.set(true)
        })
    }

    fun getLoadedApplicationList() = loadedAppList!!

    fun addOnSearchTextListener(searchTextListener: OnSearchTextListener) {
        onSearchTextListeners.add(searchTextListener)
    }

    fun removeOnSearchTextListener(searchTextListener: OnSearchTextListener) {
        onSearchTextListeners.remove(searchTextListener)
    }

    fun onSearchTextChange(newText: String?) {
        onSearchTextListeners.forEach { it.onSearchTextChange(newText) }
    }

    class Factory(val appLoader: AppLoader) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val mainViewModel = MainViewModel(appLoader)
            return mainViewModel as T
        }
    }

    interface OnSearchTextListener {
        fun onSearchTextChange(newText: String?)
    }
}
