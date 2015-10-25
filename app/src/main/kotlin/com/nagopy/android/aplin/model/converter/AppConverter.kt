package com.nagopy.android.aplin.model.converter

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.nagopy.android.aplin.entity.AppEntity
import com.nagopy.android.aplin.model.DevicePolicy
import com.nagopy.android.aplin.model.IconProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class AppConverter {

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var packageManager: PackageManager

    @Inject
    lateinit var iconProperties: IconProperties

    @Inject
    lateinit var devicePolicy: DevicePolicy

    @Inject
    lateinit var appUsageStatsManager: AppUsageStatsManager

    @Inject
    constructor() {
    }

    public fun convertToEntity(applicationInfo: ApplicationInfo): AppEntity {
        val entity: AppEntity = AppEntity(applicationInfo.packageName)

        AppParameters.values
                .filter { it.targetSdkVersion.contains(Build.VERSION.SDK_INT) }
                .forEach { param ->
                    param.setValue(entity, applicationInfo, this)
                }

        return entity
    }

    interface Converter {
        fun targetSdkVersion(): IntRange
        fun setValue(entity: AppEntity, applicationInfo: ApplicationInfo, appConverter: AppConverter)
    }

}