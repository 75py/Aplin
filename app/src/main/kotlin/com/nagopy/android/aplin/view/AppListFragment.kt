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
import android.widget.ProgressBar
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.model.DisplayItem
import com.nagopy.android.aplin.presenter.AppListPresenter
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import com.nagopy.android.aplin.view.decoration.DividerItemDecoration
import javax.inject.Inject

/**
 * カテゴリ毎のアプリ一覧を表示するフラグメント
 */
public class AppListFragment : Fragment(), AppListView {

    // ButterKnifeのresetにあたるものがないので諦める
    //    val progressBar: ProgressBar by bindView(R.id.progress)
    //    val recyclerView: RecyclerView by bindView(R.id.list)
    var progressBar: ProgressBar? = null
    var recyclerView: RecyclerView? = null

    @Inject
    lateinit var presenter: AppListPresenter

    @Inject
    lateinit var application: Application

    lateinit var adapter: AppListAdapter

    lateinit var parentView: AppListViewParent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentView = activity as AppListViewParent
        retainInstance = true
        setHasOptionsMenu(true)

        Aplin.getApplicationComponent().inject(this)

        val category = Category.valueOf(arguments.getString("type"))
        adapter = AppListAdapter(application, category, { app ->
            parentView.onListItemClick(app)
        }, { app ->
            parentView.onListItemLongClick(app)
        })
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
        progressBar = view!!.findViewById(R.id.progress) as ProgressBar
        recyclerView = view.findViewById(R.id.list) as RecyclerView

        recyclerView!!.layoutManager = LinearLayoutManager(application, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = adapter
        recyclerView!!.addItemDecoration(DividerItemDecoration(application, R.color.colorDivider, R.dimen.divider))

        if (adapter.itemCount == 0) {
            showIndicator()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView?.layoutManager = null
        recyclerView?.adapter = null
        recyclerView = null
        progressBar = null
    }

    override fun showIndicator() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideIndicator() {
        progressBar?.visibility = View.GONE
    }

    override fun showList(apps: List<AppEntity>, displayItems: List<DisplayItem>) {
        adapter.updateApplicationList(apps.toList(), displayItems)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        parentView.onOptionsItemSelected(item, adapter.filteredData)
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

