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

package com.nagopy.android.aplin.entity

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * アプリケーションを表すエンティティ
 */
class App : RealmObject() {

    @PrimaryKey
    var packageName: String = ""

    @Required
    var label: String = ""

    var isEnabled: Boolean = false

    var isSystem: Boolean = false

    var iconByteArray: ByteArray? = null

    var isInstalled: Boolean = true // 実行ユーザーでインストールされているか、API17以上で使用するフラグ。

    var isSystemPackage: Boolean = false

    var hasActiveAdmins: Boolean = false

    var isDefaultApp: Boolean = false

    var isHomeApp = false

    var firstInstallTime: Long = 0

    var lastUpdateTime: Long = 0

    var versionName: String? = null

    var permissions: RealmList<AppPermission> = RealmList()

    var isProfileOrDeviceOwner: Boolean = false // 6.0以降

    var isLaunchable: Boolean = false // ランチャーから起動可能か。権限拒否可能判定で使用する
}
