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
import rx.android.schedulers.AndroidSchedulers
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

    open fun initialize(view: MainScreenView) {
        this.view = view

        view.hideAppList()
        view.showIndicator()

        applications.initialize {
            view.hideIndicator()
            view.showAppList(userSettings.categories)
        }

        if (!analytics.isConfirmed()) {
            view.showAnalyticsConfirm()
        }
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
    }

    fun listItemClicked(activity: Activity, app: App) {
        val packageName = app.packageName.split(":")[0];
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
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
