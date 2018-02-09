package com.nagopy.android.aplin

import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.conf.ConfigurableKodein
import timber.log.Timber

class App : Application(), KodeinAware {

    override val kodein = ConfigurableKodein(mutable = true)

    override fun onCreate() {
        super.onCreate()
        resetInjection()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun resetInjection() {
        kodein.clear()
        kodein.addImport(appDependencies(), true)
    }


    private fun appDependencies(): Kodein.Module {
        return Kodein.Module(allowSilentOverride = true) {
            bind<App>() with singleton { this@App }
            bind<Handler>() with singleton { Handler(Looper.getMainLooper()) }
            bind<Resources>() with singleton { this@App.resources }
            bind<PackageManager>() with singleton { packageManager }
            bind<SharedPreferences>() with singleton { PreferenceManager.getDefaultSharedPreferences(this@App) }
            bind<DevicePolicyManager>() with singleton { getSystemService(DevicePolicyManager::class.java) }
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}
