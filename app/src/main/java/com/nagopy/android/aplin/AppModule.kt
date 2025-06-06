package com.nagopy.android.aplin

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import com.nagopy.android.aplin.ui.prefs.UserDataStore
import com.nagopy.android.aplin.ui.prefs.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule =
    module {
        single<DevicePolicyManager> { androidContext().getSystemService(DevicePolicyManager::class.java) }
        single<PackageManager> { androidContext().packageManager }
        single<ActivityManager> { androidContext().getSystemService(ActivityManager::class.java) }
        single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
        single { UserDataStore(androidContext().dataStore) }
    }
