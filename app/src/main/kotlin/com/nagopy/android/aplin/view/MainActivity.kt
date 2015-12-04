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
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import com.google.android.gms.ads.AdView
import com.nagopy.android.aplin.Aplin
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.presenter.AdPresenter
import com.nagopy.android.aplin.presenter.MainScreenPresenter
import com.nagopy.android.aplin.view.adapter.AppCategoryAdapter
import com.nagopy.android.aplin.view.adapter.MainScreenPagerAdapter
import javax.inject.Inject

/**
 * メインになる画面用のActivity
 */
public class MainActivity : AppCompatActivity(),
        MainScreenView
        , AppListViewParent // 子Viewから処理を移譲してもらうためのインターフェース
        , AdapterView.OnItemSelectedListener
        , ViewPager.OnPageChangeListener {

    val toolbar: Toolbar by lazy {
        findViewById(R.id.toolbar) as Toolbar
    }

    val spinner: Spinner by lazy { findViewById(R.id.spinner) as Spinner }

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
        spinner.adapter = AppCategoryAdapter(application)
        spinner.onItemSelectedListener = this
        viewPager.addOnPageChangeListener(this)

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
        spinner.onItemSelectedListener = null
        viewPager.removeOnPageChangeListener(this)
    }

    override fun showIndicator() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideIndicator() {
        progressBar.visibility = View.GONE
    }

    override fun showAppList() {
        viewPager.visibility = View.VISIBLE

        val adapter = MainScreenPagerAdapter(applicationContext, supportFragmentManager)
        viewPager.adapter = adapter
    }

    override fun hideAppList() {
        viewPager.visibility = View.INVISIBLE
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


    override fun onListItemClicked(app: App, category: Category) {
        presenter.listItemClicked(this, app, category)
    }

    override fun onListItemLongClicked(app: App) {
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

    // Spinner
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (viewPager.adapter != null && viewPager.currentItem != position) {
            viewPager.setCurrentItem(position, false)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    // ViewPager
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        spinner.setSelection(position)
    }


}
