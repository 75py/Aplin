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
package com.nagopy.android.aplin.model

import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import kotlin.comparisons.compareBy

/**
 * ソート順の定義クラス
 */
enum class Sort(
        val titleResourceId: Int
        , val summaryResourceId: Int
        , val targetSdkVersion: IntRange = Constants.ALL_SDK_VERSION
        , val defaultValue: Boolean = false) {

    /**
     * デフォルトのソート順。
     *
     *  1. インストール状態が異なる場合、未インストールを後ろにする。
     *  1. アプリ表示名の昇順に並べる。ただし、アプリ表示名が同一の場合はパッケージ名の昇順で並べる。
     *
     */
    DEFAULT(titleResourceId = R.string.sort_default,
            summaryResourceId = R.string.sort_default_summary,
            defaultValue = true) {
        override fun orderBy(list: Collection<App>): Collection<App> {
            return list.sortedWith(compareBy(App::isInstalled, App::label, App::packageName))
        }
    },
    /**
     * パッケージ名の昇順
     */
    PACKAGE_NAME(titleResourceId = R.string.sort_package_name
            , summaryResourceId = R.string.sort_package_name_summary) {
        override fun orderBy(list: Collection<App>): Collection<App> {
            return list.sortedWith(compareBy(App::isInstalled, App::packageName))
        }
    },
    /**
     * 初回インストール日時の降順
     */
    FIRST_INSTALL_TIME_DESC(titleResourceId = R.string.sort_install_time_desc
            , summaryResourceId = R.string.sort_install_time_desc_summary) {
        override fun orderBy(list: Collection<App>): Collection<App> {
            return list.sortedWith(compareBy({ it.firstInstallTime * -1 }, App::isInstalled, App::label, App::packageName))
        }
    },
    /**
     * 最終更新日時の降順
     */
    UPDATE_DATE_DESC(titleResourceId = R.string.sort_update_time_desc
            , summaryResourceId = R.string.sort_update_time_desc_summary) {
        override fun orderBy(list: Collection<App>): Collection<App> {
            return list.sortedWith(compareBy({ it.lastUpdateTime * -1 }, App::isInstalled, App::label, App::packageName))
        }
    };

    abstract fun orderBy(list: Collection<App>): Collection<App>

    val key: String = "${javaClass.name}_$name"
}
