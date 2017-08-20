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

import android.content.Context
import android.os.Build
import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.App
import java.text.DateFormat
import java.util.*

/**
 * アプリ毎に表示する詳細情報の定義クラス
 */
enum class DisplayItem(
        val titleResourceId: Int
        , val summaryResourceId: Int
        , val targetSdkVersion: IntRange = Constants.ALL_SDK_VERSION
        , val defaultValue: Boolean = false) {

    /**
     * インストール状態
     */
    NOT_INSTALLED(
            titleResourceId = R.string.display_item_not_installed
            , summaryResourceId = R.string.display_item_not_installed_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
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
    FIRST_INSTALL_TIME(
            titleResourceId = R.string.display_item_first_install_time
            , summaryResourceId = R.string.display_item_first_install_time_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
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
    LAST_UPDATE_TIME(
            titleResourceId = R.string.display_item_last_update_time
            , summaryResourceId = R.string.display_item_last_update_time_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
            if (appData.lastUpdateTime < Constants.Y2K) {
                return false
            }
            val format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
            sb.append(context.getString(R.string.display_item_last_update_time_format, format.format(Date(appData.lastUpdateTime))))
            return true
        }
    },
    /**
     * バージョン情報
     */
    VERSION_NAME(
            titleResourceId = R.string.display_item_version_name
            , summaryResourceId = R.string.display_item_version_name_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
            if (appData.versionName != null) {
                sb.append(context.getString(R.string.display_item_version_name_format, appData.versionName))
            }
            return true
        }
    },
    /**
     * パーミッション（インターネット）
     */
    INTERNET_PERMISSION(
            titleResourceId = R.string.display_item_internet_permission
            , summaryResourceId = R.string.display_item_internet_permission_summary) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
            if (appData.requestedPermissions.isNotEmpty()) {
                val internet = appData.requestedPermissions.contains("android.permission.INTERNET")
                if (internet) {
                    sb.append(context.getString(R.string.display_item_internet_permission_label))
                }
            }
            return true
        }
    },
    /**
     * パーミッション（拒否可能、6.0以降）
     */
    DENIABLE_PERMISSIONS(
            titleResourceId = R.string.display_item_deniable_permissions
            , summaryResourceId = R.string.display_item_deniable_permissions_summary
            , targetSdkVersion = Build.VERSION_CODES.M..Int.MAX_VALUE) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
            if (appData.requestedPermissions.isNotEmpty()) {
                val core = appData.isSystemPackage or appData.hasActiveAdmins
                if (!appData.isSystem or !core) {
                    sb.append(appData.permissionGroups.joinToString(
                            context.getString(R.string.display_item_deniable_permissions_separator)) { it.label }
                    )
                }
            }
            return true
        }
    },
    PACKAGE_NAME(
            titleResourceId = R.string.display_item_package_name
            , summaryResourceId = R.string.display_item_package_name_summary
            , targetSdkVersion = Constants.ALL_SDK_VERSION
            , defaultValue = true) {
        override fun append(context: Context, sb: StringBuilder, appData: App): Boolean {
            sb.append(appData.packageName)
            return true
        }
    }
    ;

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
    abstract fun append(context: Context, sb: StringBuilder, appData: App): Boolean

    val key: String = "${javaClass.name}_$name"
}
