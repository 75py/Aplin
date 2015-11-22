package com.nagopy.android.aplin.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.view.AppListFragment

/**
 * メイン画面のカテゴリ表示用アダプタ
 */
public class MainScreenPagerAdapter(
        val context: Context,
        fragmentManager: FragmentManager,
        val categories: List<Category>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return AppListFragment.newInstance(categories.get(position))
    }

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence = context.getString(categories.get(position).titleResourceId)
}
