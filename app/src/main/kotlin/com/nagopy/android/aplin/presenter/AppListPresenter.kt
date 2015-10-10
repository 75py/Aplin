package com.nagopy.android.aplin.presenter

import android.app.Application
import com.cookpad.android.rxt4a.schedulers.AndroidSchedulers
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.Apps
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.preference.DisplayItemSetting
import com.nagopy.android.aplin.model.preference.SortSetting
import com.nagopy.android.aplin.view.AppListView
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.functions.Func1
import rx.schedulers.Schedulers
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
    lateinit var sortSetting: SortSetting

    @Inject
    lateinit var apps: Apps

    var view: AppListView? = null

    var subscription: Subscription? = null
    var observable: Observable<List<AppEntity>>? = null
    val subscriber: Subscriber<List<AppEntity>> = object : Subscriber<List<AppEntity>>() {
        override fun onCompleted() {
        }

        override fun onError(e: Throwable) {
            Timber.e(e, "onError")
        }

        override fun onNext(appEntities: List<AppEntity>) {
            view?.hideIndicator()
            view?.showList(appEntities, displayItemSetting.value.toList())
        }
    }

    fun initialize(view: AppListView, category: Category) {
        this.view = view

        view.showIndicator()

        val sort = sortSetting.value
        observable = apps
                .getAll()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(object : Func1<AppEntity, Boolean> {
                    override fun call(appEntity: AppEntity): Boolean? {
                        return category.isTarget(appEntity)
                    }
                })
                .toSortedList { app1, app2 -> sort.compare(app1, app2) }
        subscription = observable!!.subscribe(subscriber)
    }

    override fun resume() {
        if (subscription!!.isUnsubscribed) {
            observable!!.subscribe(subscriber)
        }
    }

    override fun pause() {
        subscription!!.unsubscribe()
    }

    override fun destroy() {
        view = null
    }
}
