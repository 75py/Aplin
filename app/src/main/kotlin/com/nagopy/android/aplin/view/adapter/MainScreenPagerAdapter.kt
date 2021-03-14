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

package com.nagopy.android.aplin.view.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.aplin.view.AppListFragment

/**
 * メイン画面のカテゴリ表示用アダプタ
 */
class MainScreenPagerAdapter(
        val context: Context,
        fragmentManager: FragmentManager,
        private val categories: List<Category> = Category.getAll()) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment =
            AppListFragment.newInstance(categories[position])

    override fun getCount(): Int = categories.size

    override fun getPageTitle(position: Int): CharSequence = context.getString(categories[position].titleResourceId)
}
