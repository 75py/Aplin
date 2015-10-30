package com.nagopy.android.aplin

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import com.nagopy.android.aplin.model.preference.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ApplicationModule(val application: Application) {

    @Singleton
    @Provides
    open fun provideApplication(): Application = application

    @Singleton
    @Provides
    open fun provideSharedPreferences(application: Application): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(application)

    @Singleton
    @Provides
    open fun providePackageManager(application: Application): PackageManager = application.packageManager

    @Singleton
    @Provides
    open fun provideActivityManager(application: Application): ActivityManager
            = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    @Singleton
    @Provides
    open fun provideAppOpsManager(application: Application): AppOpsManager
            = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager


    @Provides
    @Singleton
    fun provideDisplayItemSetting(application: Application): DisplayItemSetting = GenDisplayItemSetting(application)

    @Provides
    @Singleton
    fun provideCategorySetting(application: Application): CategorySetting = GenCategorySetting(application)

    @Provides
    @Singleton
    fun provideSortSetting(application: Application): SortSetting = GenSortSetting(application)
}