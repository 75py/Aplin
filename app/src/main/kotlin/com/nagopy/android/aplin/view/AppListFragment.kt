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
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.presenter.AppListPresenter
import com.nagopy.android.aplin.view.adapter.AppListAdapter
import com.nagopy.android.aplin.view.decoration.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_app_list.*
import javax.inject.Inject

/**
 * カテゴリ毎のアプリ一覧を表示するフラグメント
 */
class AppListFragment : Fragment(), AppListView {

    @Inject
    lateinit var presenter: AppListPresenter

    @Inject
    lateinit var application: Application

    lateinit var adapter: AppListAdapter

    val category: Category by lazy { Category.valueOf(arguments.getString("type")) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)

        Aplin.getApplicationComponent().inject(this)

        adapter = AppListAdapter(presenter)

        val parentView = activity as AppListViewParent
        presenter.initialize(this, parentView, category)
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

        recyclerView.layoutManager = LinearLayoutManager(application, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(application, R.color.colorDivider, R.dimen.divider))

        fastScroller.attachRecyclerView(recyclerView)
        val appBarLayout = activity.findViewById(R.id.appBarLayout) as AppBarLayout
        fastScroller.attachAppBarLayout(
                activity.findViewById(R.id.coordinatorLayout) as CoordinatorLayout
                , appBarLayout
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.layoutManager = null
        recyclerView.adapter = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.onOptionsItemSelected(item)
        return super.onOptionsItemSelected(item)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
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

