package com.nagopy.android.aplin

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import com.github.salomonbrys.kodein.singleton
import com.nagopy.android.aplin.loader.AppLoader
import com.nagopy.android.aplin.loader.LoaderProvider
import timber.log.Timber

class App : Application(), KodeinAware {

    override val kodein = ConfigurableKodein(mutable = true)

    override fun onCreate() {
        super.onCreate()
        kodein.addImport(appDependencies(), true)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun appDependencies(): Kodein.Module {
        return Kodein.Module(allowSilentOverride = true) {
            bind<App>() with singleton { this@App }
            bind<Handler>() with singleton { Handler(Looper.getMainLooper()) }

            val loaderProvider = LoaderProvider(this@App)
            bind<AppLoader>() with singleton { loaderProvider.appLoader }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}
