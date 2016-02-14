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

package com.nagopy.android.aplin.model

import android.app.Application
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class MenuHandler {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    constructor()

    fun search(app: App): Observable<Void> {
        return Observable.create { s ->
            val actionWebSearch = Intent(Intent.ACTION_WEB_SEARCH)
                    .putExtra(SearchManager.QUERY, "${app.label} ${app.packageName}")

            if (isLaunchable(actionWebSearch)) {
                application.startActivity(actionWebSearch)
                s.onCompleted()
            } else {
                val url = "https://www.google.com/search?q=${app.label}%20${app.packageName}"
                val actionView = Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(url))
                if (isLaunchable(actionView)) {
                    application.startActivity(actionView)
                    s.onCompleted()
                } else {
                    s.onError(ActivityNotFoundException("Searchable application is not found."))
                }
            }
        }
    }

    fun share(subject: String?, text: String): Observable<Void> {
        return Observable.create { s ->
            val intent = Intent(Intent.ACTION_SEND)
                    .setType(Constants.MIME_TYPE_TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, subject ?: application.getString(R.string.app_name))
                    .putExtra(Intent.EXTRA_TEXT, text)
            if (isLaunchable(intent)) {
                application.startActivity(intent)
                s.onCompleted()
            } else {
                s.onError(ActivityNotFoundException("Shareable application is not found."))
            }
        }
    }

    private fun isLaunchable(intent: Intent): Boolean {
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()
    }


}