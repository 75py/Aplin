package com.nagopy.android.aplin.loader.internal

import android.os.IBinder
import android.webkit.IWebViewUpdateService
import timber.log.Timber

internal class AplinWebViewUpdateService {

    val webviewUpdateService: IWebViewUpdateService? by lazy {
        // 7.0-
        try {
            val clsServiceManager = Class.forName("android.os.ServiceManager")
            val clsServiceManager_getService = clsServiceManager.getDeclaredMethod("getService", java.lang.String::class.java)
            clsServiceManager_getService.isAccessible = true
            val ibinder = clsServiceManager_getService.invoke(null, "webviewupdate") as? IBinder
            val v = IWebViewUpdateService.Stub.asInterface(ibinder)
            Timber.d("webviewUpdateService = %s", v)
            return@lazy v
        } catch (e: Exception) {
            Timber.e(e, "webviewUpdateService の取得に失敗")
            return@lazy null
        }
    }

    fun isFallbackPackage(packageName: String): Boolean {
        return webviewUpdateService?.isFallbackPackage(packageName) ?: false
    }

}