/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nagopy.android.aplin

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.DeadObjectException
import android.support.multidex.MultiDex
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crash.FirebaseCrash
import timber.log.Timber


open class Aplin : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        if (component == null) {
            component = DaggerApplicationComponent.builder()
                    .applicationModule(ApplicationModule(this))
                    .build()
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(AplinDebugTree())
        } else {
            MobileAds.initialize(this, BuildConfig.AD_APP_ID)
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
                    when (priority) {
                        Log.INFO -> {
                            val b = Bundle()
                            b.putString("message", message)
                            FirebaseAnalytics.getInstance(this@Aplin).logEvent("INFO", b)
                        }
                        Log.WARN -> {
                            FirebaseCrash.log(message)
                        }
                        Log.ERROR -> {
                            FirebaseCrash.report(t)
                        }
                    }
                }
            })
        }
    }

    override fun startActivity(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        super.startActivity(intent)
    }

    companion object {
        var component: ApplicationComponent? = null
        fun getApplicationComponent(): ApplicationComponent = component!!
    }

    class AplinDebugTree : Timber.DebugTree() {
        override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
            if (t is DeadObjectException) {
                throw t
            }
            super.log(priority, tag, message, t)
        }
    }

}
