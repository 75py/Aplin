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
import com.nagopy.android.aplin.entity.names.AppNames
import com.nagopy.android.aplin.entity.names.AppNames.packageName
import com.nagopy.android.aplin.model.converter.AppConverter
import com.nagopy.android.aplin.model.converter.AppParameters
import com.nagopy.android.kotlinames.equalTo
import io.realm.Realm
import io.realm.RealmResults
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.AsyncSubject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class Applications
@Inject constructor(
        val packageManager: PackageManager
        , val appConverter: AppConverter
        , val userSettings: UserSettings
) {

    val asyncSubject = AsyncSubject.create<Void>().apply {
        Observable.create<Void> {
            if (!isLoaded()) {
                refresh()
            }
            it.onNext(null)
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    onCompleted()
                }
    }

    val enabledSettingField = ApplicationInfo::class.java.getDeclaredField("enabledSetting").apply {
        isAccessible = true
    }

    open fun isLoaded(): Boolean {
        Realm.getDefaultInstance().use {
            return it.where(App::class.java).count() > 0
        }
    }

    open fun refresh() {
        val realm = Realm.getDefaultInstance()
        realm.use {
            realm.executeTransaction {
                realm.where(App::class.java).findAll().clear()
                val allApps = getInstalledApplications()
                allApps.forEach {
                    if (shouldSkip(it)) {
                        Timber.d("skip:" + it.packageName)
                        return@forEach
                    }

                    val entity = realm.createObject(App::class.java)
                    appConverter.setValues(realm, entity, it)
                }
            }
        }
    }

    open fun getApplicationList(category: Category): RealmResults<App> {
        Realm.getDefaultInstance().use {
            Timber.d("getApplicationList " + category)
            val query = it.where(App::class.java)
            val result = userSettings.sort.findAllSortedAsync(category.where(query))
            return result
        }
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
        Timber.d("insert $pkg")
        return upsert(pkg)
    }

    open fun update(pkg: String): Observable<Void> {
        Timber.d("update $pkg")
        return upsert(pkg)
    }

    private fun upsert(pkg: String): Observable<Void> {
        return Observable.create {
            val applicationInfo = packageManager.getApplicationInfo(pkg, getFlags())
            if (!shouldSkip(applicationInfo)) {
                val realm = Realm.getDefaultInstance()
                realm.use {
                    realm.executeTransaction {
                        var entity = realm.where(App::class.java).equalTo(packageName(), pkg).findFirst()
                        if (entity == null) {
                            entity = realm.createObject(App::class.java)
                        }
                        appConverter.setValues(realm, entity, applicationInfo)
                    }
                }
            }
            it.onCompleted()
        }
    }

    open fun delete(pkg: String): Observable<Void> {
        Timber.d("delete $pkg")
        return Observable.create {
            val realm = Realm.getDefaultInstance()
            realm.use {
                realm.executeTransaction {
                    val entity = realm.where(App::class.java).equalTo(packageName(), pkg).findAll()
                    entity.clear()
                }
            }
            it.onCompleted()
        }
    }

    open fun updateDefaultAppStatus(): Observable<Void> {
        return Observable.create {
            val all = getInstalledApplications()
            val realm = Realm.getDefaultInstance()
            realm.use {
                all.forEach { applicationInfo ->
                    realm.executeTransaction {
                        val apps = realm.where(App::class.java).equalTo(AppNames.packageName(), applicationInfo.packageName).findAll()
                        if (apps.isNotEmpty()) {
                            val app = apps[0]
                            appConverter.setValue(realm, app, applicationInfo, AppParameters.isDefaultApp)
                        }
                    }
                }
            }
            it.onCompleted()
        }
    }
}
