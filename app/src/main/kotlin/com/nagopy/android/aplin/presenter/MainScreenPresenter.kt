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

package com.nagopy.android.aplin.presenter

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.*
import com.nagopy.android.aplin.view.MainScreenView
import com.nagopy.android.aplin.view.SettingsActivity
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * メイン画面用プレゼンター
 */
@Singleton
public open class MainScreenPresenter
@Inject
constructor() : Presenter {

    @Inject
    lateinit var userSettings: UserSettings

    @Inject
    lateinit var menuHandler: MenuHandler

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var applications: Applications

    var view: MainScreenView? = null


    val observer = object : Observer<Void> {
        override fun onNext(t: Void?) {
        }

        override fun onError(e: Throwable?) {
            Timber.e(e, "Error occurred")
        }

        override fun onCompleted() {
            view?.hideIndicator()
            view?.showAppList()
        }
    }
    var subscription: Subscription? = null

    open fun initialize(view: MainScreenView) {
        this.view = view

        view.hideAppList()
        view.showIndicator()

        if (!analytics.isConfirmed()) {
            view.showAnalyticsConfirm()
        }
    }

    override fun resume() {
        subscription = applications.asyncSubject.subscribe(observer)
        applications.updateDefaultAppStatus()
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    // do nothing
                }, { t ->
                    Timber.e(t, "Default status update error")
                    // ignore
                })
    }

    override fun pause() {
        subscription?.unsubscribe()
    }

    override fun destroy() {
        view = null
    }

    fun listItemClicked(activity: Activity, app: App, category: Category) {
        val packageName = app.packageName.split(":")[0];
        val intent = when (category) {
            Category.SYSTEM_ALERT_WINDOW_PERMISSION ->
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            else ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))

        }
        activity.startActivity(intent);

        analytics.click(app.packageName)
    }

    fun listItemLongClicked(app: App) {
        menuHandler.search(app)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { e ->
                    Timber.e(e, "onError")
                    Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()
                })

        analytics.longClick(app.packageName)
    }

    fun onMenuItemClicked(item: MenuItem, checkedItemList: List<App>) {
        val onNext: (Void) -> Unit = {}
        val onError: (Throwable) -> Unit = { e ->
            Timber.e(e, "onError")
            Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()
        }

        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(application, SettingsActivity::class.java)
                application.startActivity(intent)
            }
            R.id.action_share_label -> {
                val text = SharingMethod.LABEL.makeShareString(checkedItemList)
                menuHandler.share(item.title?.toString(), text)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext, onError)
            }
            R.id.action_share_package_name -> {
                val text = SharingMethod.PACKAGE.makeShareString(checkedItemList)
                menuHandler.share(item.title?.toString(), text)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext, onError)
            }
            R.id.action_share_label_and_package_name -> {
                val text = SharingMethod.LABEL_AND_PACKAGE.makeShareString(checkedItemList)
                menuHandler.share(item.title?.toString(), text)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext, onError)
            }
        }

        analytics.menuClick(item.title.toString())
    }
}
