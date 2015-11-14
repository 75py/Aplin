package com.nagopy.android.aplin

import com.nagopy.android.aplin.view.AppListFragment
import com.nagopy.android.aplin.view.MainActivity
import com.nagopy.android.aplin.view.PackageChangedReceiver
import com.nagopy.android.aplin.view.SettingsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    open fun inject(mainActivity: MainActivity)

    fun inject(appListFragment: AppListFragment)

    fun inject(settingsActivity: SettingsActivity)

    open fun inject(packageChangedReceiver: PackageChangedReceiver)
}