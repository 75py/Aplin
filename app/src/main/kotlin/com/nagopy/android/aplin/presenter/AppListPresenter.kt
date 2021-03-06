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
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Applications
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.model.UserSettings
import com.nagopy.android.aplin.view.AppListView
import com.nagopy.android.aplin.view.AppListViewParent
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎アプリ一覧のプレゼンター
 */
open class AppListPresenter @Inject constructor() : Presenter {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var userSettings: UserSettings

    @Inject
    lateinit var applications: Applications

    @Inject
    lateinit var iconHelper: IconHelper

    var defList: List<App> = emptyList()

    val filteredList: MutableList<App> = ArrayList()

    var view: AppListView? = null // onDestroyでnullにするため、NULL可

    var parentView: AppListViewParent? = null // onDestroyでnullにするため、NULL可

    lateinit var category: Category

    var searchText: String = ""

    private val compositeDisposable = CompositeDisposable()

    fun initialize(view: AppListView, parentView: AppListViewParent, category: Category) {
        this.view = view
        this.parentView = parentView
        this.category = category
        updateAppList()
    }

    fun updateAppList() {
        applications.getApplicationList(category)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    defList = it
                    updateFilter(searchText)
                }, { e ->
                    Timber.e(e, "Error occurred")
                }).also {
                    compositeDisposable.add(it)
                }
    }

    fun updateFilter(searchText: String) {
        this.searchText = searchText
        Timber.d("searchText: %s, defList.size = %d, applications.appCache.size = %d", searchText, defList.size, applications.appCache.size)
        filteredList.clear()
        defList.forEach {
            if (it.packageName.contains(searchText, ignoreCase = true)
                    || it.label.contains(searchText, ignoreCase = true)) {
                Timber.v("Hit: %s", it)
                filteredList.add(it)
            }
        }
        view?.notifyDataSetChanged()
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
        parentView = null
        compositeDisposable.clear()
    }

    fun onOptionsItemSelected(item: MenuItem) {
        parentView?.onOptionsItemSelected(item, filteredList)
    }

    fun onItemViewCreated(holder: AppListAdapter.ViewHolder, position: Int) {
        val entity = filteredList[position]

        val textColor = ContextCompat.getColor(application,
                if (entity.isEnabled) R.color.text_color else R.color.textColorTertiary)

        holder.label.text = entity.label
        holder.label.setTextColor(textColor)

        val sb = StringBuilder()
        for (item in userSettings.displayItems) {
            if (item.append(application, sb, entity)) {
                sb.append(Constants.LINE_SEPARATOR)
            }
        }

        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
            var infoString = sb.toString().trim()
            infoString = infoString.replace((Constants.LINE_SEPARATOR + "+").toRegex(), Constants.LINE_SEPARATOR)
            holder.status.text = infoString
            holder.status.visibility = View.VISIBLE
        } else {
            holder.status.text = ""
            holder.status.visibility = View.GONE
        }
        holder.status.setTextColor(textColor)

        synchronized(holder.icon) {
            if (holder.icon.tag != entity.packageName) {
                holder.icon.tag = entity.packageName
                holder.icon.visibility = View.INVISIBLE
                iconHelper.requestLoadIcon(entity.packageName)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ icon ->
                            synchronized(holder.icon) {
                                if (holder.icon.tag == entity.packageName) {
                                    holder.icon.setImageDrawable(icon)
                                    holder.icon.visibility = View.VISIBLE
                                    Timber.v("Set icon. pkg=%s", entity.packageName)
                                }
                            }
                        })
            }
        }

        holder.icon.scaleType = ImageView.ScaleType.FIT_CENTER
        holder.icon.layoutParams.width = iconHelper.iconSize
        holder.icon.layoutParams.height = iconHelper.iconSize
    }

    fun onItemClicked(position: Int) {
        val app = filteredList[position]
        Timber.d("open %s", app)
        parentView?.onListItemClicked(app, category)
    }

    fun onItemLongClicked(position: Int) {
        val app = filteredList[position]
        parentView?.onListItemLongClicked(app)
    }

}
