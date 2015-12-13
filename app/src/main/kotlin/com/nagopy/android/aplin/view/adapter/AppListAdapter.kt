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

package com.nagopy.android.aplin.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.presenter.AppListPresenter


open class AppListAdapter(val appListPresenter: AppListPresenter) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        appListPresenter.onAttachedToRecyclerView()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        appListPresenter.onDetachedFromRecyclerView()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(appListPresenter.application).inflate(R.layout.list_item, parent, false)
        val holder = ViewHolder(view)
        appListPresenter.onCreateViewHolder(holder)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        appListPresenter.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int = appListPresenter.getItemCount()

    open class ViewHolder : RecyclerView.ViewHolder {

        open val parent: View
        open val icon: ImageView
        open val label: TextView
        open val status: TextView

        constructor(parentView: View) : super(parentView) {
            parent = parentView
            icon = parentView.findViewById(R.id.icon) as ImageView
            label = parentView.findViewById(R.id.label) as TextView
            status = parentView.findViewById(R.id.status) as TextView
        }

    }
}
