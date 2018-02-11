package com.nagopy.android.aplin.loader.internal

class TmpAppInfo {

    var packageName: String = ""

    var label: String = ""

    var enabled: Boolean = false

    var isSystem: Boolean = false

    var isInstalled: Boolean = true // 実行ユーザーでインストールされているか、API17以上で使用するフラグ。

    var isSystemPackage: Boolean = false

    var hasActiveAdmins: Boolean = false

    var isDefaultApp: Boolean = false

    var isHomeApp = false

    var firstInstallTime: Long = 0

    var lastUpdateTime: Long = 0

    var versionName: String? = null

    //var requestedPermissions: MutableList<String> = ArrayList()

    //var permissionGroups: MutableList<PermissionGroup> = ArrayList()

    var isProfileOrDeviceOwner: Boolean = false // 6.0以降

    var isLaunchable: Boolean = false // ランチャーから起動可能か。権限拒否可能判定で使用する

    var isDisabledUntilUsed: Boolean = false // 6.0-

    var shouldSkip: Boolean = false

    var isFallbackPackage: Boolean = false

    override fun toString(): String = "$packageName    $label"

}