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

package com.nagopy.android.aplin.view

import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.presenter.AppListPresenter
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * カテゴリ毎のアプリ一覧を表示するフラグメント
 */
class AppListFragment : Fragment(), AppListView, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    lateinit var listView: AbsListView

    @Inject
    lateinit var presenter: AppListPresenter

    @Inject
    lateinit var application: Application

    lateinit var adapter: AppListAdapter

    var disposer: Disposable? = null
    var searchTextDisposer: Disposable? = null

    val category: Category by lazy { Category.valueOf(arguments!!.getString("type")!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        Aplin.getApplicationComponent().inject(this)

        adapter = AppListAdapter(application, presenter)

        presenter.initialize(this, activity as AppListViewParent, category)

        disposer = presenter.applications.appObserver
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("App changed. category = %s", category)
                    presenter.updateAppList()
                }, { e ->
                    Timber.e(e, "Error")
                })

        searchTextDisposer = MainActivity.searchTextObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    Timber.v("searchTextObserver %s", t)
                    presenter.updateFilter(t)
                }
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
        listView.onItemClickListener = this
        listView.onItemLongClickListener = this
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
        listView.onItemClickListener = null
        listView.onItemLongClickListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        if (disposer?.isDisposed == false) {
            disposer?.dispose()
        }
        if (searchTextDisposer?.isDisposed == false) {
            searchTextDisposer?.dispose()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.list)
        listView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Timber.d("Click pos=%s", position)
        presenter.onItemClicked(position)
    }

    override fun onItemLongClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        Timber.d("Long-click pos=%s", position)
        presenter.onItemLongClicked(position)
        return true
    }

    companion object {
        fun newInstance(category: Category): AppListFragment {
            val appListFragment = AppListFragment()
            val args = Bundle()
            args.putString("type", category.name)
            appListFragment.arguments = args
            return appListFragment
        }
    }
}

