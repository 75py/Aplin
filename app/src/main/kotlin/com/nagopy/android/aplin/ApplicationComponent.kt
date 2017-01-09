/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    fun inject(mainActivity: MainActivity)

    fun inject(appListFragment: AppListFragment)

    fun inject(settingsActivity: SettingsActivity)

    fun inject(packageChangedReceiver: PackageChangedReceiver)
}