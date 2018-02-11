package com.nagopy.android.aplin

import android.app.Application
import android.content.Intent
import android.support.v7.widget.RecyclerView
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidActivityScope
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import com.nagopy.android.aplin.loader.AppLoader
import com.nagopy.android.aplin.loader.LoaderProvider
import timber.log.Timber

class App : Application(), KodeinAware {

    override val kodein = ConfigurableKodein(mutable = true)

    override fun onCreate() {
        super.onCreate()
        kodein.addImport(appModule(), true)
        kodein.addImport(viewModelModule(), true)
        kodein.addImport(activityModule(), true)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(androidActivityScope.lifecycleManager)
    }

    private fun appModule(): Kodein.Module {
        return Kodein.Module(allowSilentOverride = true) {
            val loaderProvider = LoaderProvider(this@App)
            bind<AppLoader>() with singleton { loaderProvider.appLoader }
        }
    }

    private fun viewModelModule(): Kodein.Module {
        return Kodein.Module(true) {
            bind<MainViewModel.Factory>() with singleton { MainViewModel.Factory(instance()) }
            bind<AppListViewModel.Factory>() with singleton { AppListViewModel.Factory() }
        }
    }

    private fun activityModule(): Kodein.Module {
        return Kodein.Module {
            bind<Navigator>() with scopedSingleton(androidActivityScope) {
                Navigator(androidActivityScope.getContext())
            }
            bind<RecyclerView.RecycledViewPool>() with scopedSingleton(androidActivityScope) { RecyclerView.RecycledViewPool() }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}
