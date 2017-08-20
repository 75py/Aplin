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

import android.app.ActivityManager
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.util.LruCache
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * アイコン表示用のプロパティを扱うクラス
 */
@Singleton
open class IconHelper
/**
 * コンストラクタ
 */
@Inject
constructor(val application: Application, activityManager: ActivityManager) {

    @Inject
    lateinit var packageManager: PackageManager

    /**
     * デフォルト表示用のアプリケーションアイコン
     */
    val defaultIcon: Drawable = ResourcesCompat.getDrawable(application.resources, android.R.drawable.sym_def_app_icon, null)!!

    /**
     * アイコン画像の大きさ（PX)
     */
    open val iconSize: Int = activityManager.launcherLargeIconSize * 4 / 3

    val iconCache: IconLruCache = IconLruCache(1024 * 1024 * activityManager.memoryClass / 6)

    fun requestLoadIcon(pkg: String): Single<Drawable> {
        return Single.create<Drawable> {
            var icon: Drawable? = iconCache.getOrNull(pkg)
            if (icon == null) {
                val d: BitmapDrawable?
                try {
                    d = packageManager.getApplicationIcon(pkg) as? BitmapDrawable
                } catch (e: PackageManager.NameNotFoundException) {
                    Timber.d(e, "Error pkg=%s", pkg)
                    it.onSuccess(defaultIcon)
                    return@create
                }
                if (d != null) {
                    icon = d
                } else {
                    icon = defaultIcon
                }
                iconCache.put(pkg, icon)
                Timber.d("Add cache. pkg=s%s", pkg)
            } else {
                Timber.d("Cached. pkg=%s", pkg)
            }
            it.onSuccess(icon)
        }
    }

    class IconLruCache(maxSize: Int) : LruCache<String, Drawable>(maxSize) {

        fun getOrNull(key: String): Drawable? = get(key)

        override fun sizeOf(key: String?, value: Drawable): Int {
            if (value is BitmapDrawable) {
                return value.bitmap.byteCount / 1024
            } else {
                return 0;
            }
        }
    }
}
