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
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.view.preference.MultiSelectionItem
import java.text.DateFormat
import java.util.*

/**
 * アプリ毎に表示する詳細情報の定義クラス
 */
public enum class DisplayItem
/**
 * コンストラクタ
 * @param titleResourceId   設定画面で表示するタイトルの文字列リソースID
 * @param summaryResourceId 設定画面で表示する説明文の文字列リソースID
 */
(private val titleResourceId: Int, private val summaryResourceId: Int) : MultiSelectionItem {

    /**
     * インストール状態
     */
    NOT_INSTALLED(R.string.display_item_not_installed, R.string.display_item_not_installed_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean {
            if (!appData.isInstalled) {
                sb.append(context.getString(R.string.display_item_not_installed_format))
                return true
            }
            return false
        }
    },
    /**
     * 初回インストール日時
     */
    FIRST_INSTALL_TIME(R.string.display_item_first_install_time, R.string.display_item_first_install_time_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean {
            if (appData.firstInstallTime < Constants.Y2K) {
                return false
            }
            val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
            sb.append(context.getString(R.string.display_item_first_install_time_format, format.format(Date(appData.firstInstallTime))))
            return true
        }
    },
    /**
     * 最終更新日時
     */
    LAST_UPDATE_TIME(R.string.display_item_last_update_time, R.string.display_item_last_update_time_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean {
            if (appData.lastUpdateTime < Constants.Y2K) {
                return false
            }
            val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
            sb.append(context.getString(R.string.display_item_last_update_time_format, format.format(Date(appData.lastUpdateTime))))
            return true
        }
    },
    /**
     * 最近使用した回数（Lollipop以降）
     */
    RECENTLY_USED_COUNT(R.string.display_item_recently_used_count, R.string.display_item_recently_used_count_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean {
            if (appData.launchTimes <= 0) {
                return false
            }
            sb.append(context.getString(R.string.display_item_recently_used_count_format, appData.launchTimes))
            return true
        }

        override fun minSdkVersion(): Int = Build.VERSION_CODES.LOLLIPOP

    },
    /**
     * バージョン情報
     */
    VERSION_NAME(R.string.display_item_version_name, R.string.display_item_version_name_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean {
            if (appData.versionName != null) {
                sb.append(context.getString(R.string.display_item_version_name_format, appData.versionName))
            }
            return true
        }
    }
    ;

    override fun getTitle(context: Context): String = context.getString(titleResourceId)

    override fun getSummary(context: Context): String = context.getString(summaryResourceId)

    override fun minSdkVersion(): Int = Build.VERSION_CODES.BASE

    override fun maxSdkVersion(): Int = Integer.MAX_VALUE

    /**
     * 定義された情報をStringBuilderに追加する。

     * @param context Context
     * *
     * @param sb      StringBuilder
     * *
     * @param appData アプリ情報
     * *
     * @return 文字列を追加した場合はtrue、追加しなかった場合はfalse
     */
    public abstract fun append(context: Context, sb: StringBuilder, appData: AppEntity): Boolean

}
