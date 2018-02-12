package com.nagopy.android.aplin.loader.internal

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.UserHandle
import com.nagopy.android.aplin.loader.AppInfo
import timber.log.Timber
import java.lang.reflect.Field
import java.text.Collator

internal class InternalAppLoader(val packageManager: PackageManager
                                 , val aplinDevicePolicyManager: AplinDevicePolicyManager
                                 , val packageNamesLoader: PackageNamesLoader
                                 , val iconLoader: IconLoader
                                 , val aplinWebViewUpdateService: AplinWebViewUpdateService
                                 , val aplinPackageManager: AplinPackageManager) {

    lateinit var homeActivities: Collection<String>
    lateinit var launcherPkgs: List<String>
    val enabledSettingField: Field? by lazy {
        try {
            ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
                isAccessible = true
            }
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun prepare() {
        homeActivities = aplinPackageManager.getHomePackages()
        Timber.v("homeActivities %s", homeActivities)

        val launcherIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        launcherPkgs =
                try {
                    packageManager.queryIntentActivities(launcherIntent, 0).map { it.activityInfo.packageName }
                } catch (e: Exception) {
                    Timber.w(e, "Error: queryIntentActivities")
                    emptyList()
                }
        Timber.v("launcherPkgs %s", launcherPkgs)
    }

    fun loadPackage(packageName: String): AppInfo {
        val packageInfo = getPackageInfo(packageName)
        val tmpAppInfo = TmpAppInfo()
        AppParameter.values().forEach { p ->
            p.setValue(tmpAppInfo, ConvertInfo(
                    packageManager
                    , aplinDevicePolicyManager
                    , enabledSettingField
                    , homeActivities
                    , launcherPkgs
                    , aplinWebViewUpdateService
                    , packageInfo
            ))
        }
        return AppInfo(
                packageName = tmpAppInfo.packageName
                , label = tmpAppInfo.label
                , isEnabled =
        if (tmpAppInfo.isSystem) {
            if (tmpAppInfo.isFallbackPackage) {
                false
            } else if (tmpAppInfo.isHomeApp || tmpAppInfo.isSystemPackage) {
                true
            } else if (tmpAppInfo.enabled && !tmpAppInfo.isDisabledUntilUsed) {
                true
            } else {
                false
            }
        } else {
            // TODO ユーザー数を見るべきっぽいが、可能？
            tmpAppInfo.enabled
        }, isDisablable =
        tmpAppInfo.isSystem && !tmpAppInfo.isProfileOrDeviceOwner && !tmpAppInfo.isSystemPackage && !tmpAppInfo.hasActiveAdmins && !tmpAppInfo.isHomeApp
                , isSystem = tmpAppInfo.isSystem
                , icon = iconLoader.loadIcon(tmpAppInfo.packageName)
        )
    }

    fun load(): List<AppInfo> {
        val appList = ArrayList<AppInfo>()
        prepare()
        packageNamesLoader.getInstalledPackageNames().forEach { packageName ->
            appList.add(loadPackage(packageName))
        }
        return appList
    }

    fun getPackageInfo(packageName: String): PackageInfo {
        // 一度に取得する量が多いとエラーになるらしいので、細かく分けて取得する
        val packageInfo = packageManager.getPackageInfo(
                packageName,
                flags
        )

        try {
            val pkg = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS or flags
            )
            packageInfo.requestedPermissions = pkg.requestedPermissions
        } catch (e: Exception) {
            Timber.w(e)
            return packageInfo
        }

        try {
            val pkg = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES or flags
            )
            packageInfo.signatures = pkg.signatures
        } catch (e: Exception) {
            Timber.w(e)
            return packageInfo
        }

        return packageInfo
    }

    class ConvertInfo(val packageManager: PackageManager
                      , val aplinDevicePolicyManager: AplinDevicePolicyManager
                      , val enabledSettingField: Field?
                      , val homeActivities: Collection<String>
                      , val launcherPkgs: List<String>
                      , val aplinWebViewUpdateService: AplinWebViewUpdateService
                      , val packageInfo: PackageInfo) {
    }

    enum class AppParameter() {
        packageName {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.packageName = convertInfo.packageInfo.applicationInfo.packageName
            }
        },
        label {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.label = convertInfo.packageInfo.applicationInfo.loadLabel(convertInfo.packageManager).toString()
            }
        },
        enabled {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.enabled = convertInfo.packageInfo.applicationInfo.enabled
            }
        },
        isSystem {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isSystem =
                        (convertInfo.packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        || (convertInfo.packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            }
        },
        isSystemPackage {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isSystemPackage = convertInfo.aplinDevicePolicyManager.isSystemPackage(convertInfo.packageInfo)
            }
        },
        firstInstallTime {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.firstInstallTime = convertInfo.packageInfo.firstInstallTime
            }
        },
        lastUpdateTime {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.lastUpdateTime = convertInfo.packageInfo.lastUpdateTime
            }
        },
        hasActiveAdmins {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.hasActiveAdmins = convertInfo.aplinDevicePolicyManager.packageHasActiveAdmins(convertInfo.packageInfo.applicationInfo.packageName)
            }
        },
        isInstalled {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isInstalled = (convertInfo.packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_INSTALLED) != 0
            }
        },
        isDefaultApp {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                val outFilters = ArrayList<IntentFilter>()
                val outActivities = ArrayList<ComponentName>()
                convertInfo.packageManager.getPreferredActivities(outFilters, outActivities, convertInfo.packageInfo.applicationInfo.packageName)
                tmpAppInfo.isDefaultApp = !outActivities.isEmpty()
            }
        },
        isHomeApp {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isHomeApp = convertInfo.homeActivities.contains(convertInfo.packageInfo.applicationInfo.packageName)
            }
        },
        versionName {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.versionName = convertInfo.packageInfo.versionName
            }
        },
        isProfileOrDeviceOwner {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isProfileOrDeviceOwner = //Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        convertInfo.aplinDevicePolicyManager.isProfileOrDeviceOwner(convertInfo.packageInfo.applicationInfo.packageName)
            }
        },
        isLaunchable {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isLaunchable = convertInfo.launcherPkgs.contains(convertInfo.packageInfo.applicationInfo.packageName)
            }
        },
        isDisabledUntilUsed {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val enabledSetting = convertInfo.enabledSettingField?.get(convertInfo.packageInfo.applicationInfo)
                tmpAppInfo.isDisabledUntilUsed = enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED
            }
        },
        shouldSkip {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.shouldSkip = false
                if (convertInfo.packageInfo.applicationInfo.packageName.isEmpty()) {
                    tmpAppInfo.shouldSkip = true
                } else if (!convertInfo.packageInfo.applicationInfo.enabled) {
                    // 無効になっていて、かつenabledSettingが3でないアプリは除外する
                    val enabledSetting = convertInfo.enabledSettingField?.get(convertInfo.packageInfo.applicationInfo)
                    if (enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                        tmpAppInfo.shouldSkip = true
                    }
                }
            }
        },
        isFallbackPackage {
            override fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo) {
                tmpAppInfo.isFallbackPackage = convertInfo.aplinWebViewUpdateService.isFallbackPackage(convertInfo.packageInfo.packageName)
            }
        };

        abstract fun setValue(tmpAppInfo: TmpAppInfo, convertInfo: ConvertInfo)
    }

    companion object {

        /**
         * [android.content.pm.PackageManager.getInstalledApplications]の引数に使うフラグ。以下のクラスを参照
         * /packages/apps/Settings/src/com/android/settings/applications/ApplicationsState.java
         */
        val flags: Int by lazy {
            //@Suppress("DEPRECATION")
            val ownerRetrieveFlags = PackageManager.MATCH_UNINSTALLED_PACKAGES or
                    PackageManager.MATCH_DISABLED_COMPONENTS or
                    PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS

            val retrieveFlags = PackageManager.MATCH_DISABLED_COMPONENTS or
                    PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS

            /*
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val myUserIdMethod = UserHandle::class.java.getDeclaredMethod("myUserId")
                if (myUserIdMethod.invoke(null) == 0) {
                    return@lazy ownerRetrieveFlags
                } else {
                    return@lazy retrieveFlags
                }
            } else {
            */
            val myUserHandle = android.os.Process.myUserHandle()
            val isOwnerMethod = UserHandle::class.java.getDeclaredMethod("isOwner")
            if (isOwnerMethod.invoke(myUserHandle) as Boolean) {
                return@lazy ownerRetrieveFlags
            } else {
                return@lazy retrieveFlags
            }
            /*}*/
        }
    }

}