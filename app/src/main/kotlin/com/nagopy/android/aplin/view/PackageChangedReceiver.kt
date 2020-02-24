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

package com.nagopy.android.aplin.view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.model.Applications
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PackageChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var applications: Applications

    override fun onReceive(context: Context, intent: Intent) {
        Aplin.getApplicationComponent().inject(this)

        val pkg = intent.data?.schemeSpecificPart

        Timber.d("%s", intent)
        if (pkg != null) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> applications.insert(pkg)
                Intent.ACTION_PACKAGE_CHANGED -> applications.update(pkg)
                Intent.ACTION_PACKAGE_REPLACED -> applications.update(pkg)
                Intent.ACTION_PACKAGE_REMOVED -> applications.delete(pkg)
                Intent.ACTION_PACKAGE_FULLY_REMOVED -> applications.delete(pkg)
                else -> throw IllegalAccessException("Unknown action: ${intent.action}")
            }.subscribeOn(Schedulers.newThread())
                    .subscribe({
                        // do nothing
                    }, { t ->
                        Timber.e(t, "Receiver error")
                        // ignore
                    })

        }
    }
}