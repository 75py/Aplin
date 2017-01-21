package com.nagopy.android.aplin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.presenter.AppListPresenter
import io.realm.RealmBaseAdapter

class AppListAdapter(context: Context, val appListPresenter: AppListPresenter)
    : RealmBaseAdapter<App>(context, appListPresenter.realmResults) {

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