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
import io.realm.annotations.Required

/**
 * アプリケーションを表すエンティティ
 */
open class App : RealmObject() {

    @Required
    open var packageName: String = ""

    @Required
    open var label: String = ""

    open var isEnabled: Boolean = false

    open var isSystem: Boolean = false

    open var iconByteArray: ByteArray? = null

    open var isInstalled: Boolean = true // 実行ユーザーでインストールされているか、API17以上で使用するフラグ。

    open var isThisASystemPackage: Boolean = false

    open var hasActiveAdmins: Boolean = false

    open var isDefaultApp: Boolean = false

    open var firstInstallTime: Long = 0

    open var lastUpdateTime: Long = 0

    open var versionName: String? = null

    open var permissions: RealmList<AppPermission> = RealmList()

    open var isProfileOrDeviceOwner: Boolean = false // 6.0以降
}
