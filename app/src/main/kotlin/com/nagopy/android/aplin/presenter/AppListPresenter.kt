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

import android.app.Application
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.UserSettings
import com.nagopy.android.aplin.view.AppListView
import io.realm.Realm
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎アプリ一覧のプレゼンター
 */
public class AppListPresenter : Presenter, Applications.PackageChangedListener {

    @Inject
    constructor() {
    }

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var userSettings: UserSettings

    @Inject
    lateinit var applications: Applications

    lateinit var realm: Realm

    var view: AppListView? = null

    lateinit var category: Category

    fun initialize(view: AppListView, category: Category) {
        realm = Realm.getInstance(application)
        this.view = view
        this.category = category

        val appEntities = applications.getApplicationList(category)
        Timber.d("appEntities " + appEntities)
        view.showList(appEntities, userSettings.displayItems)
        applications.addPackageChangedListener(this)
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        applications.removePackageChangedListener(this)
        view = null
        realm.close()
    }


    override fun onPackageChanged() {
        val appEntities = applications.getApplicationList(category)
        view?.showList(appEntities, userSettings.displayItems)
    }

}
