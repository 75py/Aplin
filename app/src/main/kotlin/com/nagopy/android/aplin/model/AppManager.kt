package com.nagopy.android.aplin.model

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserHandle
import com.nagopy.android.aplin.constants.VersionCode
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.converter.AppConverter
import com.nagopy.android.aplin.model.converter.AppUsageStatsManager
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AppManager {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    lateinit var appConverter: AppConverter

    @Inject
    lateinit var appUsageStatsManager: AppUsageStatsManager

    val enabledSettingField: FieldReflection<Int>

    init {
        enabledSettingField = FieldReflection(ApplicationInfo::class.java, "enabledSetting")
    }

    @Inject
    constructor()

    fun getAll(): List<AppEntity> {
        // 取得する際のフラグ
        // 設定画面のフラグ＋無効化可能判定用にシグネチャ
        val mRetrieveFlags = RETRIEVE_FLAGS or PackageManager.GET_SIGNATURES

        val applicationInfo = packageManager.getInstalledApplications(mRetrieveFlags)

        val apps = ArrayList<AppEntity>(applicationInfo.size())
        for (info in applicationInfo) {
            if (!info.enabled) {
                // 無効になっていて、かつenabledSettingが3でないアプリは除外する
                val enabledSetting = enabledSettingField.get(info)
                if (enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                    Timber.d("skip:" + info.packageName)
                    continue
                }
            }

            val entity = appConverter.convertToEntity(info)
            apps.add(entity)
        }
        return apps

    }

    companion object {

        val RETRIEVE_FLAGS = getRetrieveFlags()

        /**
         * [android.content.pm.PackageManager.getInstalledApplications]の引数に使う値を返す.
         * 以下のクラスを参照。
         * /packages/apps/Settings/src/com/android/settings/applications/ApplicationsState.java
         */
        fun getRetrieveFlags(): Int {
            if (Build.VERSION.SDK_INT <= VersionCode.JELLY_BEAN) {
                // 4.1以下
                return PackageManager.GET_UNINSTALLED_PACKAGES or PackageManager.GET_DISABLED_COMPONENTS
            }

            // 4.2以上
            // > Only the owner can see all apps.
            // とのことなので、IDが0（＝オーナー）は全部見られる、的なフラグ設定らしい
            val myUserIdMethod = MethodReflection<Int>(UserHandle::class.java, "myUserId")
            val myUserId = myUserIdMethod.invoke(null)
            if (myUserId == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    // 4.3以上
                    return PackageManager.GET_UNINSTALLED_PACKAGES or PackageManager.GET_DISABLED_COMPONENTS or PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS
                } else {
                    // 4.2
                    return PackageManager.GET_UNINSTALLED_PACKAGES or PackageManager.GET_DISABLED_COMPONENTS
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    // 4.3以上
                    return PackageManager.GET_DISABLED_COMPONENTS or PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS
                } else {
                    // 4.2
                    return PackageManager.GET_DISABLED_COMPONENTS
                }
            }
        }
    }


}
