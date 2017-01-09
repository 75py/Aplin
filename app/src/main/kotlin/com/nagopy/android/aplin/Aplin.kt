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
import android.support.multidex.MultiDex
import android.util.Log
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.StandardExceptionParser
import com.google.android.gms.analytics.Tracker
import io.realm.Realm
import io.realm.RealmConfiguration
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
            Timber.plant(Timber.DebugTree())
        } else {
            val ga = GoogleAnalytics.getInstance(this)
            val tracker = ga.newTracker(getString(R.string.ga_trackingId))
            tracker.enableExceptionReporting(true)
            tracker.enableAdvertisingIdCollection(true)
            tracker.enableAutoActivityTracking(true)
            Aplin.tracker = tracker
            Timber.plant(AnalyticsTree(this))
        }

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(BuildConfig.VERSION_CODE.toLong())
                .deleteRealmIfMigrationNeeded()
                .build())
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

        var tracker: Tracker? = null
    }

    class AnalyticsTree(val application: Application) : Timber.Tree() {

        val exceptionParser = StandardExceptionParser(application, null)

        override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
            if (priority == Log.ERROR) {
                Aplin.tracker?.send(
                        HitBuilders.ExceptionBuilder()
                                .setDescription(exceptionParser.getDescription(Thread.currentThread().name, t))
                                .setFatal(false)
                                .build()
                )
            }
        }

    }
}
