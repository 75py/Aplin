package com.nagopy.android.aplin

import com.nagopy.android.aplin.view.AppListFragment
import com.nagopy.android.aplin.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(appListFragment: AppListFragment)

}