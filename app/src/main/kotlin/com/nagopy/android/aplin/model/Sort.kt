/*
 * Copyright (C) 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagopy.android.aplin.model

import android.content.Context
import android.os.Build
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.easyprefs.SingleSelectionItem
import java.util.*

/**
 * ソート順の定義クラス
 */
public enum class Sort
/**
 * コンストラクタ

 * @param titleResourceId   設定画面で表示するタイトルの文字列リソースID
 * *
 * @param summaryResourceId 設定画面で表示する説明文の文字列リソースID
 */
(private val titleResourceId: Int, private val summaryResourceId: Int) : Comparator<AppEntity>, SingleSelectionItem {

    /**
     * デフォルトのソート順。
     *
     *  1. インストール状態が異なる場合、未インストールを後ろにする。
     *  1. アプリ表示名の昇順に並べる。ただし、アプリ表示名が同一の場合はパッケージ名の昇順で並べる。
     *
     */
    DEFAULT(R.string.sort_default, R.string.sort_default_summary) {
        override fun compare(lhs: AppEntity, rhs: AppEntity): Int {
            if (lhs.isInstalled != rhs.isInstalled) {
                // 「未インストール」は最後に
                return if (lhs.isInstalled) -1 else 1
            }

            val label0 = lhs.label
            val label1 = rhs.label

            var ret = label0.compareTo(label1, ignoreCase = true)
            // ラベルで並び替え、同じラベルがあったらパッケージ名で
            if (ret == 0) {
                val pkgName0 = lhs.packageName
                val pkgName1 = rhs.packageName
                ret = pkgName0.compareTo(pkgName1, ignoreCase = true)
            }
            return ret
        }
    },
    /**
     * パッケージ名の昇順
     */
    PACKAGE_NAME(R.string.sort_package_name, R.string.sort_package_name_summary) {
        override fun compare(lhs: AppEntity, rhs: AppEntity): Int {
            if (lhs.isInstalled != rhs.isInstalled) {
                // 「未インストール」は最後に
                return if (lhs.isInstalled) -1 else 1
            }
            return lhs.packageName.compareTo(rhs.packageName, ignoreCase = true)
        }
    },
    /**
     * 最終更新日時の降順
     */
    UPDATE_DATE_DESC(R.string.sort_update_time_desc, R.string.sort_update_time_desc_summary) {
        override fun compare(lhs: AppEntity, rhs: AppEntity): Int {
            if (lhs.lastUpdateTime != rhs.lastUpdateTime) {
                val l = lhs.lastUpdateTime - rhs.lastUpdateTime
                return if (l > 0) -1 else 1
            }
            return DEFAULT.compare(lhs, rhs)
        }
    };

    override fun getTitle(context: Context): String = context.getString(titleResourceId)

    override fun getSummary(context: Context): String = context.getString(summaryResourceId)

    override fun minSdkVersion(): Int = Build.VERSION_CODES.BASE

    override fun maxSdkVersion(): Int = Integer.MAX_VALUE
}
