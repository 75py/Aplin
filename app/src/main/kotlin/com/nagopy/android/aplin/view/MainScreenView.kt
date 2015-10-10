package com.nagopy.android.aplin.view

import com.nagopy.android.aplin.model.Category

/**
 * メイン画面の親ビューを表すインターフェース
 */
public interface MainScreenView {

    /**
     * インジケーターを表示する
     */
    public fun showIndicator()

    /**
     * インジケーターを非表示にする
     */
    public fun hideIndicator()

    /**
     * カテゴリ別アプリ一覧を表示する

     * @param categories 表示カテゴリ一覧
     */
    public fun showAppList(categories: List<Category>)

    /**
     * カテゴリ別アプリ一覧を非表示にする
     */
    public fun hideAppList()

}
