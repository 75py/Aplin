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

import com.nagopy.android.aplin.constants.Constants
import com.nagopy.android.aplin.entity.AppEntity

/**
 * 共有方法の定義クラス
 */
public enum class SharingMethod {
    /**
     * アプリ名共有
     */
    LABEL {
        override fun makeShareString(appList: List<AppEntity>): String {
            val sb = StringBuilder()
            for (app in appList) {
                sb.append(makeShareString(app))
                sb.append(Constants.LINE_SEPARATOR)
            }
            return sb.toString()
        }

        override fun makeShareString(appData: AppEntity): String {
            return appData.label
        }
    },
    /**
     * パッケージ名
     */
    PACKAGE {
        override fun makeShareString(appList: List<AppEntity>): String {
            val sb = StringBuilder()
            for (app in appList) {
                sb.append(makeShareString(app))
                sb.append(Constants.LINE_SEPARATOR)
            }
            return sb.toString()
        }

        override fun makeShareString(appData: AppEntity): String {
            return appData.packageName
        }
    },
    /**
     * アプリ名とパッケージ名
     */
    LABEL_AND_PACKAGE {
        override fun makeShareString(appList: List<AppEntity>): String {
            val sb = StringBuilder()
            for (app in appList) {
                sb.append(makeShareString(app))
                sb.append(Constants.LINE_SEPARATOR)
                sb.append(Constants.LINE_SEPARATOR)
            }
            return sb.toString()
        }

        override fun makeShareString(appData: AppEntity): String {
            return appData.label + Constants.LINE_SEPARATOR + appData.packageName
        }
    };

    /**
     * 共有用の文字列を作成する

     * @param appList 共有対象
     * *
     * @return 共有用の文字列
     */
    public abstract fun makeShareString(appList: List<AppEntity>): String

    /**
     * 共有用の文字列を作成する

     * @param appData 共有対象
     * *
     * @return 共有用の文字列
     */
    public abstract fun makeShareString(appData: AppEntity): String

}
