package com.nagopy.android.aplin.data.repository

import android.app.admin.DevicePolicyManager

interface DevicePolicyRepository {
    /**
     * [DevicePolicyManager]のpackageHasActiveAdminsメソッドを実行する
     * @param packageName パッケージ名
     * *
     * @return packageHasActiveAdminsの結果を返す。
     * * エラーがあった場合はfalseを返す。
     */
    fun packageHasActiveAdmins(packageName: String): Boolean

    fun isProfileOrDeviceOwner(packageName: String): Boolean
}
