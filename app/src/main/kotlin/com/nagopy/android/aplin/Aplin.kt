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
import android.content.Intent
import android.util.Log
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
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
            Timber.plant(CrashlyticsTree())
        }

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
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
    }

    class CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String?, t: Throwable?) {
            when (priority) {
                Log.ERROR -> {
                    Crashlytics.log(priority, tag, message)
                    Crashlytics.logException(t)
                }
                Log.WARN, Log.INFO -> {
                    Crashlytics.log(priority, tag, message)
                }
                else -> {
                    // ignore
                }
            }

        }

    }
}
