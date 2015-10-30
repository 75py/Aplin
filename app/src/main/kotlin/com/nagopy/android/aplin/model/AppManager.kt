package com.nagopy.android.aplin.model

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserHandle
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

    val enabledSettingField: FieldReflection<Int> = FieldReflection(ApplicationInfo::class.java, "enabledSetting")

    @Inject
    constructor()

    fun getAll(): List<AppEntity> {
        val applicationInfo = getInstalledApplications()
        val apps = ArrayList<AppEntity>(applicationInfo.size)
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

    /**
     * アプリケーション一覧を取得する.<br>
     * [android.content.pm.PackageManager.getInstalledApplications]の引数については、以下のクラスを参照
     * /packages/apps/Settings/src/com/android/settings/applications/ApplicationsState.java
     */
    fun getInstalledApplications(): List<ApplicationInfo> {
        val ownerRetrieveFlags = PackageManager.GET_UNINSTALLED_PACKAGES or
                PackageManager.GET_DISABLED_COMPONENTS or
                PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS

        val retrieveFlags = PackageManager.GET_DISABLED_COMPONENTS or
                PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS

        val flags: Int
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val myUserIdMethod = UserHandle::class.java.getDeclaredMethod("myUserId")
            flags = if (myUserIdMethod.invoke(null) == 0) {
                ownerRetrieveFlags
            } else {
                retrieveFlags
            }
        } else {
            val myUserHandle = android.os.Process.myUserHandle()
            val isOwnerMethod = UserHandle::class.java.getDeclaredMethod("isOwner")
            flags = if (isOwnerMethod.invoke(myUserHandle) as Boolean) {
                ownerRetrieveFlags
            } else {
                retrieveFlags
            }
        }
        return packageManager.getInstalledApplications(flags or PackageManager.GET_SIGNATURES)
    }
}
