package com.nagopy.android.aplin.view

import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.DisplayItem
import com.nagopy.android.aplin.model.IconHelper
import com.nagopy.android.aplin.presenter.AppListPresenter
import com.nagopy.android.aplin.view.adapter.RealmAppAdapter
import com.nagopy.android.aplin.view.decoration.DividerItemDecoration
import io.realm.RealmResults
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎のアプリ一覧を表示するフラグメント
 */
public class AppListFragment : Fragment(), AppListView {

    // ButterKnifeのresetにあたるものがないので諦める
    //    val recyclerView: RecyclerView by bindView(R.id.list)
    var recyclerView: RecyclerView? = null

    @Inject
    lateinit var presenter: AppListPresenter

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var iconHelper: IconHelper

    var adapter: RealmAppAdapter? = null

    lateinit var parentView: AppListViewParent

    val category: Category by lazy { Category.valueOf(arguments.getString("type")) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentView = activity as AppListViewParent
        retainInstance = true
        setHasOptionsMenu(true)

        Aplin.getApplicationComponent().inject(this)

        presenter.initialize(this, category)
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view!!.findViewById(R.id.list) as RecyclerView

        recyclerView!!.layoutManager = LinearLayoutManager(application, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = adapter
        recyclerView!!.addItemDecoration(DividerItemDecoration(application, R.color.colorDivider, R.dimen.divider))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView?.layoutManager = null
        recyclerView?.adapter = null
        recyclerView = null
    }

    override fun showList(apps: RealmResults<AppEntity>, displayItems: List<DisplayItem>) {
        if (adapter == null) {
            adapter = RealmAppAdapter(application, category, iconHelper, { app ->
                parentView.onListItemClick(app)
            }, { app ->
                parentView.onListItemLongClick(app)
            })
        }
        adapter!!.realmResults = apps
        adapter!!.displayItems = displayItems
        adapter!!.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        parentView.onOptionsItemSelected(item, adapter!!.realmResults)
        return super.onOptionsItemSelected(item);
    }

    companion object {
        public fun newInstance(category: Category): AppListFragment {
            val appListFragment = AppListFragment()
            val args = Bundle()
            args.putString("type", category.name)
            appListFragment.arguments = args
            return appListFragment
        }
    }
}

