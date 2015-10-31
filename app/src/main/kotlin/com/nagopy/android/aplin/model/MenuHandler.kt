package com.nagopy.android.aplin.model

import android.app.Application
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.AppEntity
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public open class MenuHandler {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    constructor()

    public fun search(app: AppEntity): Observable<Void> {
        return Observable.create(Observable.OnSubscribe { s ->
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
        })
    }

    public fun share(subject: String?, text: String): Observable<Void> {
        return Observable.create(Observable.OnSubscribe<Void> { s ->
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
        })
    }

    private fun isLaunchable(intent: Intent): Boolean {
        return !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()
    }


}