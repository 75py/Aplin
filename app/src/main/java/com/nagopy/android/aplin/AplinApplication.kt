package com.nagopy.android.aplin

import android.app.Application
import com.nagopy.android.aplin.data.repository.repositoryModule
import com.nagopy.android.aplin.domain.usecase.useCaseModule
import com.nagopy.android.aplin.ui.uiModule
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@Suppress("unused")
class AplinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

        startKoin {
            // androidLogger()
            androidContext(this@AplinApplication)
            modules(appModule, dispatcherModule, uiModule, useCaseModule, repositoryModule)
        }
    }
}
