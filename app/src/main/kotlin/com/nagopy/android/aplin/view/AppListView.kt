package com.nagopy.android.aplin.view

import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.DisplayItem
import io.realm.RealmResults

/**
 * アプリ一覧のビューを表すインターフェース
 */
public interface AppListView {

    /**
     * アプリ一覧を表示する

     * @param apps    アプリケーション
     * *
     * @param displayItems 表示項目
     */
    public fun showList(apps: RealmResults<App>, displayItems: List<DisplayItem>)

}
