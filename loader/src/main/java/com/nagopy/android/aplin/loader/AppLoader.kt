package com.nagopy.android.aplin.loader

import com.nagopy.android.aplin.loader.internal.InternalAppLoader

class AppLoader internal constructor(val internalAppLoader: InternalAppLoader) {

    fun load(): List<AppInfo> = internalAppLoader.load()

    fun load(packageName: String): AppInfo {
        internalAppLoader.prepare()
        return internalAppLoader.loadPackage(packageName)
    }

}
