package com.nagopy.android.aplin.entity

import android.graphics.drawable.Drawable
import kotlin.properties.Delegates

/**
 * アプリケーションを表すエンティティ
 */
data class AppEntity {

    var label: String by Delegates.notNull<String>()

    var packageName: String  by Delegates.notNull<String>()

    var isEnabled: Boolean  by Delegates.notNull<Boolean>()

    var isSystem: Boolean  by Delegates.notNull<Boolean>()

    var icon: Drawable by Delegates.notNull<Drawable>()

    var isInstalled: Boolean = true // 実行ユーザーでインストールされているか、API17以上で使用するフラグ。

    var isThisASystemPackage: Boolean = false

    var hasActiveAdmins: Boolean = false

    var isDefaultApp: Boolean = false

    var firstInstallTime: Long = 0

    var lastUpdateTime: Long = 0

    /** UsageStatsManagerで取得 */
    var launchTimes: Int = 0

    var versionName: String? = null

}
