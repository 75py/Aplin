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
import android.os.Build
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.MenuHandler
import com.nagopy.android.aplin.model.SharingMethod
import com.nagopy.android.aplin.view.MainScreenView
import com.nagopy.android.aplin.view.SettingsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * メイン画面用プレゼンター
 */
@Singleton
open class MainScreenPresenter @Inject constructor() : Presenter {

    @Inject
    lateinit var menuHandler: MenuHandler

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var applications: Applications

    var view: MainScreenView? = null

    private val compositeDisposable = CompositeDisposable()

    open fun initialize(view: MainScreenView) {
        this.view = view

        view.hideAppList()
        view.showIndicator()
        view.setToolbarSpinnerEnabled(false)

        applications.initAppCache()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.hideIndicator()
                    view.showAppList()
                    view.setToolbarSpinnerEnabled(true)
                }, { e ->
                    Timber.e(e, "Error occurred")
                }).also {
                    compositeDisposable.add(it)
                }
    }

    override fun resume() {
        applications.updatePoco()
                .subscribeOn(Schedulers.computation())
                .subscribe({
                    // do nothing
                }, { t ->
                    Timber.e(t, "Default status update error")
                    // ignore
                }).also {
                    compositeDisposable.add(it)
                }
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
        compositeDisposable.clear()
    }

    fun listItemClicked(activity: Activity, app: App, category: Category) {
        val packageName = app.packageName.split(":")[0]

        val intent = when (category) {
            Category.SYSTEM_ALERT_WINDOW_PERMISSION ->
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            else ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (activity.isInMultiWindowMode) {
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
        try {
            activity.startActivity(intent)
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    fun listItemLongClicked(app: App) {
        menuHandler.search(app)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { e ->
                    Timber.e(e, "onError")
                    Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()
                }).also {
                    compositeDisposable.add(it)
                }
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
    }
}
