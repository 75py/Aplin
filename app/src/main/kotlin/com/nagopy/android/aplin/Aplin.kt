package com.nagopy.android.aplin

import android.app.Application
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
    }

    companion object {
        var component: ApplicationComponent? = null
        fun getApplicationComponent(): ApplicationComponent {
            return component!!
        }
    }
}