package com.nagopy.android.aplin.view

import com.nagopy.android.aplin.entity.AppEntity
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
    public fun showList(apps: RealmResults<AppEntity>, displayItems: List<DisplayItem>)

}
