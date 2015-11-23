package com.nagopy.android.aplin.entity

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

    /** UsageStatsManagerで取得 */
    open var launchTimes: Int = 0

    open var versionName: String? = null

}
