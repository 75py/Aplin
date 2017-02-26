package com.nagopy.android.aplin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.presenter.AppListPresenter

class AppListAdapter(val context: Context, val appListPresenter: AppListPresenter) : BaseAdapter() {

    override fun getItem(position: Int): Any = appListPresenter.filteredList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = appListPresenter.filteredList.count()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view)
        view.tag = holder

        appListPresenter.onItemViewCreated(holder, position)

        return view
    }

    open class ViewHolder(parentView: View) {
        open val icon: ImageView = parentView.findViewById(R.id.icon) as ImageView
        open val label: TextView = parentView.findViewById(R.id.label) as TextView
        open val status: TextView = parentView.findViewById(R.id.status) as TextView
    }

}