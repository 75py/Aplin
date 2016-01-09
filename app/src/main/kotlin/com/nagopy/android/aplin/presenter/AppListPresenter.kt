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
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import io.realm.Realm
import io.realm.RealmResults
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎アプリ一覧のプレゼンター
 */
open class AppListPresenter : Presenter {

    @Inject
    constructor() {
    }

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var userSettings: UserSettings

    @Inject
    lateinit var applications: Applications

    @Inject
    lateinit var iconHelper: IconHelper

    lateinit var realm: Realm

    lateinit var realmResults: RealmResults<App>

    var view: AppListView? = null // onDestroyでnullにするため、NULL可

    var parentView: AppListViewParent? = null // onDestroyでnullにするため、NULL可

    lateinit var category: Category

    fun initialize(view: AppListView, parentView: AppListViewParent, category: Category) {
        realm = Realm.getDefaultInstance()
        this.view = view
        this.parentView = parentView
        this.category = category

        realmResults = applications.getApplicationList(category)
    }

    override fun resume() {
    }

    override fun pause() {
    }

    override fun destroy() {
        view = null
        parentView = null
        realm.close()
    }

    fun onAttachedToRecyclerView() {
        realmResults.removeChangeListeners()
        Timber.v("addChangeListener category=$category")
        realmResults.addChangeListener {
            view?.notifyDataSetChanged()
        }
    }

    fun onDetachedFromRecyclerView() {
        Timber.v("removeChangeListeners category=$category")
        realmResults.removeChangeListeners()

    }

    fun onCreateViewHolder(holder: AppListAdapter.ViewHolder) {
        holder.parent.setOnClickListener { view ->
            if (holder.adapterPosition == RecyclerView.NO_POSITION) {
                Timber.w("adapterPosition == -1. Do nothing.")
                view.setOnClickListener(null)
                return@setOnClickListener
            }

            parentView?.onListItemClicked(realmResults[holder.adapterPosition], category)
        }
        holder.parent.setOnLongClickListener { view ->
            if (holder.adapterPosition == RecyclerView.NO_POSITION) {
                Timber.w("adapterPosition == -1. Do nothing.")
                view.setOnLongClickListener(null)
                return@setOnLongClickListener false
            }

            parentView?.onListItemLongClicked(realmResults[holder.adapterPosition])
            return@setOnLongClickListener true
        }

        holder.icon.scaleType = ImageView.ScaleType.FIT_CENTER
        holder.icon.layoutParams.width = iconHelper.iconSize
        holder.icon.layoutParams.height = iconHelper.iconSize
    }

    fun onBindViewHolder(holder: AppListAdapter.ViewHolder, position: Int) {
        val entity = realmResults[position]

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
        if (sb.length > 0) {
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

        holder.icon.setImageDrawable(iconHelper.getIcon(entity))
    }

    open fun getItemCount(): Int = realmResults.size

    fun onOptionsItemSelected(item: MenuItem) {
        parentView?.onOptionsItemSelected(item, realmResults)
    }

}
