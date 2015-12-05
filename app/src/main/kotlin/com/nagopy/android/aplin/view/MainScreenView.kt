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

/**
 * メイン画面の親ビューを表すインターフェース
 */
interface MainScreenView {

    /**
     * インジケーターを表示する
     */
    open fun showIndicator()

    /**
     * インジケーターを非表示にする
     */
    open fun hideIndicator()

    /**
     * カテゴリ別アプリ一覧を表示する
     */
    open fun showAppList()

    /**
     * カテゴリ別アプリ一覧を非表示にする
     */
    open fun hideAppList()

    open fun showAnalyticsConfirm()

}
