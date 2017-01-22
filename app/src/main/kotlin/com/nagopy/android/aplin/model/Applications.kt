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

package com.nagopy.android.aplin.model

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserHandle
import com.nagopy.android.aplin.entity.App
import com.nagopy.android.aplin.model.converter.AppConverter
import com.nagopy.android.aplin.model.converter.AppParameters
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Applications
@Inject constructor(
        val packageManager: PackageManager
        , val appConverter: AppConverter
        , val userSettings: UserSettings
) {
    val appCache: ConcurrentHashMap<String, App> = ConcurrentHashMap()

    val appObserver: PublishSubject<Int> = PublishSubject.create<Int>() // onNext(null)が不可になったので、ダミー引数Intを使う

    val enabledSettingField: Field = ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
        isAccessible = true
    }

    open fun isLoaded(): Boolean {
        return appCache.isNotEmpty()
    }

    open fun initAppCache(): Completable {
        return Completable.create {
            if (!isLoaded()) {
                refresh()
            }
            it.onComplete()
        }
    }

    open fun refresh() {
        appCache.clear()
        val allApps = getInstalledApplications()
        val executorService = Executors.newCachedThreadPool()
        appConverter.prepare()
        allApps.forEach {
            Timber.d("LOAD start pkg=%s", it.packageName)
            executorService.execute {
                if (shouldSkip(it)) {
                    Timber.d("skip: %s", it.packageName)
                    //return@forEach
                } else {
                    val entity = App()
                    appConverter.setValues(entity, it)
                    appCache.put(it.packageName, entity)
                    Timber.d("ADDCACHE pkg=%s", entity.packageName)
                }
                Timber.d("LOAD fin  pkg=%s", it.packageName)
            }
        }
        executorService.shutdown()
        executorService.awaitTermination(60, TimeUnit.SECONDS)

    }

    open fun getApplicationList(category: Category): List<App> {
        Timber.d("getApplicationList %s", category)
        return userSettings.sort.orderBy(category.where(appCache.values)).toList()
    }

    /**
     * アプリケーション一覧を取得する.<br>
     * [android.content.pm.PackageManager.getInstalledApplications]の引数については、以下のクラスを参照
     * /packages/apps/Settings/src/com/android/settings/applications/ApplicationsState.java
     */
    fun getInstalledApplications(): List<ApplicationInfo> {
        return packageManager.getInstalledApplications(getFlags())
    }

    open fun getFlags(): Int {
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
        return flags or PackageManager.GET_SIGNATURES
    }

    open fun shouldSkip(applicationInfo: ApplicationInfo): Boolean {
        if (applicationInfo.packageName.isEmpty()) {
            return true
        }
        if (!applicationInfo.enabled) {
            // 無効になっていて、かつenabledSettingが3でないアプリは除外する
            val enabledSetting = enabledSettingField.get(applicationInfo)
            if (enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                return true
            }
        }
        return false
    }

    open fun insert(pkg: String): Observable<Void> {
        Timber.d("insert %s", pkg)
        return upsert(pkg)
    }

    open fun update(pkg: String): Observable<Void> {
        Timber.d("update %s", pkg)
        return upsert(pkg)
    }

    private fun upsert(pkg: String): Observable<Void> {
        return Observable.create {
            val applicationInfo = packageManager.getApplicationInfo(pkg, getFlags())
            if (!shouldSkip(applicationInfo)) {
                val entity = App()
                appConverter.prepare()
                appConverter.setValues(entity, applicationInfo)
                appCache.put(pkg, entity)
            }
            it.onComplete()

            appObserver.onNext(0)
        }
    }

    open fun delete(pkg: String): Observable<Void> {
        Timber.d("delete %s", pkg)
        return Observable.create {
            appCache.remove(pkg)
            it.onComplete()

            appObserver.onNext(0)
        }
    }

    open fun updatePoco(): Observable<Void> {
        return Observable.create {
            val all = getInstalledApplications()
            var updated = false
            appConverter.prepare()
            synchronized(appCache) {
                all.forEach { applicationInfo ->
                    val app = appCache[applicationInfo.packageName]
                    if (app != null) {
                        val newApp = App()
                        appConverter.setValues(newApp, applicationInfo, AppParameters.isDefaultApp, AppParameters.isEnabled)

                        if (newApp.isDefaultApp != app.isDefaultApp || newApp.isEnabled != app.isEnabled) {
                            app.isDefaultApp = newApp.isDefaultApp
                            app.isEnabled = app.isEnabled
                            appCache.put(applicationInfo.packageName, app)
                            updated = true
                        }
                    }
                }
            }
            it.onComplete()

            if (updated) {
                Timber.d("Updated!")
                appObserver.onNext(0)
            }
        }
    }
}
