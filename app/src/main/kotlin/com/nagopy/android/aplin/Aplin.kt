package com.nagopy.android.aplin

import android.app.Application
import timber.log.Timber

class Aplin : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}