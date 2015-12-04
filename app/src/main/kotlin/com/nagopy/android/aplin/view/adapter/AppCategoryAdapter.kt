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

import android.app.Application
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Category

class AppCategoryAdapter(val application: Application) : BaseAdapter() {

    internal val categories = Category.values().filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }

    val inflater = LayoutInflater.from(application)

    override fun getCount(): Int {
        return categories.size
    }

    override fun getItem(position: Int): Category {
        return categories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View
        if (convertView == null || convertView.tag.toString().equals("DROPDOWN")) {
            view = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false)
            view.tag = "DROPDOWN"
        } else {
            view = convertView
        }
        val titleView = view.findViewById(android.R.id.text1) as TextView
        titleView.text = getTitle(position)
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null || convertView.tag.toString().equals("NON_DROPDOWN")) {
            view = inflater.inflate(R.layout.toolbar_spinner_item_actionbar, parent, false);
            view.tag = "NON_DROPDOWN"
        } else {
            view = convertView
        }
        val textView: TextView = view.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)
        return view
    }

    private fun getTitle(position: Int): String {
        return application.getString(getItem(position).titleResourceId)
    }
}
