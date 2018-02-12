package com.nagopy.android.aplin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.databinding.ObservableBoolean
import com.nagopy.android.aplin.loader.AppInfo
import com.nagopy.android.aplin.loader.AppLoader
import timber.log.Timber

class MainViewModel(private val appLoader: AppLoader
                    , private val navigator: Navigator) : ViewModel() {

    val isLoaded = ObservableBoolean(false)

    private var loadedAppList: MutableMap<String, AppInfo>? = null
    private val onSearchTextListeners = ArrayList<OnSearchTextListener>()
    private val onAppInfoChangeListeners = ArrayList<OnAppInfoChangeListener>()
    private var openedPackageName: String? = null

    fun loadApplications() {
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

            loadedAppList = HashMap()
            appList.forEach { loadedAppList!![it.packageName] = it }
            isLoaded.set(true)
        })
    }

    fun getLoadedApplicationList() = loadedAppList!!.values

    fun onAppClick(packageName: String) {
        Timber.d("onAppClick %s", packageName)
        navigator.startApplicationDetailSettings(packageName)
        openedPackageName = packageName
    }

    fun loadPackageIfNeeded() {
        openedPackageName?.let {
            loadPackage(it)
        }
        openedPackageName = null
    }

    private fun loadPackage(packageName: String) {
        Timber.i("loadPackage %s", packageName)
        try {
            val newAppInfo = appLoader.load(packageName)
            val oldAppInfo = loadedAppList?.put(packageName, newAppInfo)
            Timber.i("%s", newAppInfo)
            Timber.i("%s", oldAppInfo)
            Timber.i("%s", oldAppInfo != newAppInfo)
            if (newAppInfo != oldAppInfo) {
                onAppChange(packageName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.d(e)
            loadedAppList?.remove(packageName)
            onAppRemove(packageName)
        }
    }

    fun addOnAppInfoChangeListener(onAppInfoChangeListener: OnAppInfoChangeListener) {
        onAppInfoChangeListeners.add(onAppInfoChangeListener)
    }

    fun removeOnAppInfoChangeListener(onAppInfoChangeListener: OnAppInfoChangeListener) {
        onAppInfoChangeListeners.remove(onAppInfoChangeListener)
    }

    fun onAppChange(packageName: String) {
        Timber.d("onAppChange %s", packageName)
        onAppInfoChangeListeners.forEach {
            it.onAppChange(packageName)
        }
    }

    fun onAppRemove(packageName: String) {
        Timber.d("onAppRemove %s", packageName)
        onAppInfoChangeListeners.forEach {
            it.onAppRemove(packageName)
        }
    }

    fun addOnSearchTextListener(searchTextListener: OnSearchTextListener) {
        onSearchTextListeners.add(searchTextListener)
    }

    fun removeOnSearchTextListener(searchTextListener: OnSearchTextListener) {
        onSearchTextListeners.remove(searchTextListener)
    }

    fun onSearchTextChange(newText: String?) {
        onSearchTextListeners.forEach { it.onSearchTextChange(newText) }
    }

    class Factory(val appLoader: AppLoader, val navigator: Navigator) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val mainViewModel = MainViewModel(appLoader, navigator)
            return mainViewModel as T
        }
    }

    interface OnSearchTextListener {
        fun onSearchTextChange(newText: String?)
    }

    interface OnAppInfoChangeListener {
        fun onAppChange(packageName: String)
        fun onAppRemove(packageName: String)
    }
}
