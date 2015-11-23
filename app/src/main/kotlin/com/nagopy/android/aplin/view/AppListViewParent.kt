package com.nagopy.android.aplin.view

import android.view.MenuItem
import com.nagopy.android.aplin.entity.App

/**
 * AppListViewの親Viewのインターフェース<br>
 * 主にメニュー操作などの処理を移譲するために使用。
 */
public interface AppListViewParent {
    fun onListItemClick(app: App)

    fun onListItemLongClick(app: App)

    fun onOptionsItemSelected(item: MenuItem, appList: List<App>)

}
