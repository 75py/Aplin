package com.nagopy.android.aplin.presenter

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.Apps
import com.nagopy.android.aplin.model.MenuHandler
import com.nagopy.android.aplin.model.SharingMethod
import com.nagopy.android.aplin.model.preference.CategorySetting
import com.nagopy.android.aplin.view.MainScreenView
import com.nagopy.android.aplin.view.SettingsActivity
import rx.Subscriber
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * メイン画面用プレゼンター
 */
public open class MainScreenPresenter
@Inject
constructor() : Presenter {

    @Inject
    lateinit var categorySetting: CategorySetting

    @Inject
    lateinit var apps: Apps

    @Inject
    lateinit var menuHandler: MenuHandler

    @Inject
    lateinit var application: Application

    var view: MainScreenView? = null

    open fun initialize(view: MainScreenView) {
        this.view = view
        view.hideAppList()
        view.showIndicator()

        // キャッシュにのせる
        // TODO もう少しちゃんとやる
        apps.getAll()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<AppEntity>() {
                    override fun onCompleted() {
                        view.hideIndicator()
                        view.showAppList(categorySetting.value.toList())
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e, "onError")
                    }

                    override fun onNext(appEntity: AppEntity) {
                    }
                })
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
    }

    fun listItemClicked(activity: Activity, app: AppEntity) {
        val packageName = app.packageName.split(":")[0];
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName))
        activity.startActivity(intent);
    }

    fun listItemLongClicked(app: AppEntity) {
        menuHandler.search(app)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { e ->
                    Timber.e(e, "onError")
                    Toast.makeText(application, e.message, Toast.LENGTH_LONG).show()
                })
    }

    fun onMenuItemClicked(item: MenuItem, checkedItemList: List<AppEntity>) {
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
            R.id.action_search -> {
                menuHandler.search(checkedItemList.first())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext, onError)
            }
        }
    }
}
