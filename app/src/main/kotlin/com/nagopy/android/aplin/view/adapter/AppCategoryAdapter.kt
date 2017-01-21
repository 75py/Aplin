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
import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Category

class AppCategoryAdapter(val application: Application) : BaseAdapter() {

    internal val categories = Category.getAll()

    val inflater = LayoutInflater.from(application)!!

    val screenWidth: Int

    init {
        val wm = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val point = Point()
        display.getSize(point)
        screenWidth = point.x
    }

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
        if (convertView == null || convertView.tag.toString() == "DROPDOWN") {
            view = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, parent, false)
            view.tag = "DROPDOWN"
        } else {
            view = convertView
        }

        val category = getItem(position)

        val titleView = view.findViewById(android.R.id.text1) as TextView
        titleView.setText(category.titleResourceId)

        val summaryTextView: TextView = view.findViewById(android.R.id.text2) as TextView
        summaryTextView.setText(category.summaryResourceId)
        summaryTextView.visibility = if (summaryTextView.text.isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
        summaryTextView.maxWidth = screenWidth * 7 / 10

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null || convertView.tag.toString() == "NON_DROPDOWN") {
            view = inflater.inflate(R.layout.toolbar_spinner_item_actionbar, parent, false)
            view.tag = "NON_DROPDOWN"
        } else {
            view = convertView
        }
        val textView: TextView = view.findViewById(android.R.id.text1) as TextView
        val category = getItem(position)
        textView.setText(category.titleResourceId)

        return view
    }
}
