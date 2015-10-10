package com.nagopy.android.aplin

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
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
    fun providePackageManager(application: Application): PackageManager = application.packageManager

    @Singleton
    @Provides
    fun provideActivityManager(application: Application): ActivityManager
            = application.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


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