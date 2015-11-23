package com.nagopy.android.aplin

import android.app.Application
import android.content.Intent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

open class Aplin : Application() {

    override fun onCreate() {
        super.onCreate()

        if (component == null ) {
            component = DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(this))
                    .build()
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (BuildConfig.PRODUCTION) {
            Fabric.with(this, Crashlytics())
        }
    }

    override fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        super.startActivity(intent)
    }

    companion object {
        var component: ApplicationComponent? = null
        fun getApplicationComponent(): ApplicationComponent {
            return component!!
        }
    }
}
