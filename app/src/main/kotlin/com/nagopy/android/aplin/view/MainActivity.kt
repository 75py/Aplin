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

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.*
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.presenter.AdPresenter
import com.nagopy.android.aplin.presenter.MainScreenPresenter
import com.nagopy.android.aplin.view.adapter.MainScreenPagerAdapter
import javax.inject.Inject

/**
 * メインになる画面用のActivity
 */
public class MainActivity : AppCompatActivity(),
        MainScreenView
        , AppListViewParent // 子Viewから処理を移譲してもらうためのインターフェース
{

    val toolbar: Toolbar by lazy {
        findViewById(R.id.toolbar) as Toolbar
    }

    val tabLayout: TabLayout by lazy { findViewById(R.id.tab) as TabLayout }

    val viewPager: ViewPager by lazy { findViewById(R.id.pager) as ViewPager }

    val adView: AdView by lazy { findViewById(R.id.adView) as AdView }

    val progressBar: ProgressBar by lazy { findViewById(R.id.progress) as ProgressBar }

    @Inject
    lateinit var presenter: MainScreenPresenter

    @Inject
    lateinit var adPresenter: AdPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Aplin.getApplicationComponent().inject(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        presenter.initialize(this)
        adPresenter.initialize(adView)
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
        adPresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
        adPresenter.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        adPresenter.destroy()
    }

    override fun showIndicator() {
        progressBar.visible()
        tabLayout.gone()
    }

    override fun hideIndicator() {
        progressBar.gone()
        tabLayout.visible()
    }

    override fun showAppList(categories: List<Category>) {
        viewPager.visible()
        tabLayout.visible()

        val adapter = MainScreenPagerAdapter(applicationContext, supportFragmentManager, categories)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun hideAppList() {
        viewPager.invisible()
        tabLayout.invisible()
    }

    // menu ===============================================================================================

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem, appList: List<App>) {
        presenter.onMenuItemClicked(item, appList)
    }


    override fun onListItemClick(app: App) {
        presenter.listItemClicked(this, app)
    }

    override fun onListItemLongClick(app: App) {
        presenter.listItemLongClicked(app)
    }

    // GA =================================================================================================
    override fun showAnalyticsConfirm() {
        AlertDialog.Builder(this)
                .setTitle(R.string.ga_confirm_dialog_title)
                .setMessage(R.string.ga_confirm_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.ga_confirm_dialog_agree, { dialog, i -> presenter.analytics.agree() })
                .setNegativeButton(R.string.ga_confirm_dialog_disagree, { dialog, i -> presenter.analytics.disagree() })
                .show();
    }
}
