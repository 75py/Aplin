package com.nagopy.android.aplin.presenter

import android.app.Application
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.preference.DisplayItemSetting
import com.nagopy.android.aplin.view.AppListView
import io.realm.Realm
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎アプリ一覧のプレゼンター
 */
public class AppListPresenter : Presenter {

    @Inject
    constructor() {
    }

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var displayItemSetting: DisplayItemSetting

    @Inject
    lateinit var applications: Applications

    lateinit var realm: Realm

    var view: AppListView? = null

    fun initialize(view: AppListView, category: Category) {
        realm = Realm.getInstance(application)
        this.view = view

        val appEntities = applications.getApplicationList(category)
        Timber.d("appEntities " + appEntities)
        view.showList(appEntities, displayItemSetting.value.toList())
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
        realm.close()
    }
}
