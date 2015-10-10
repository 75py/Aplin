package com.nagopy.android.aplin.view

import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.DisplayItem

/**
 * アプリ一覧のビューを表すインターフェース
 */
public interface AppListView {

    /**
     * インジケーターを表示する
     */
    public fun showIndicator()

    /**
     * インジケーターを非表示にする
     */
    public fun hideIndicator()

    /**
     * アプリ一覧を表示する

     * @param apps    アプリケーション
     * *
     * @param displayItems 表示項目
     */
    public fun showList(apps: List<AppEntity>, displayItems: List<DisplayItem>)

}
