package com.nagopy.android.aplin.model.converter

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.DevicePolicy
import com.nagopy.android.aplin.model.IconHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppConverter {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    lateinit var iconHelper: IconHelper

    @Inject
    lateinit var devicePolicy: DevicePolicy

    @Inject
    lateinit var appUsageStatsManager: AppUsageStatsManager

    @Inject
    constructor() {
    }

    open fun setValues(appEntity: AppEntity, applicationInfo: ApplicationInfo) {
        AppParameters.values
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach { param ->
                    param.setValue(appEntity, applicationInfo, this)
                }
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(entity: AppEntity, applicationInfo: ApplicationInfo, appConverter: AppConverter)
    }

}