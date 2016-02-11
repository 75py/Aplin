package com.nagopy.android.aplin.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.presenter.AppListPresenter

class LegacyAppListAdapter(val appListPresenter: AppListPresenter) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: LayoutInflater.from(appListPresenter.application).inflate(R.layout.list_item, parent, false)
        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view)
        view.tag = holder

        appListPresenter.onItemViewCreated(holder, position)

        return view
    }

    override fun getItem(position: Int): App {
        return appListPresenter.realmResults[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int = appListPresenter.getItemCount()


    open class ViewHolder {

        open val parent: View
        open val icon: ImageView
        open val label: TextView
        open val status: TextView

        constructor(parentView: View) {
            parent = parentView
            icon = parentView.findViewById(R.id.icon) as ImageView
            label = parentView.findViewById(R.id.label) as TextView
            status = parentView.findViewById(R.id.status) as TextView
        }

    }
}