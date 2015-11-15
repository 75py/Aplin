package com.nagopy.android.aplin.model

import android.app.ActivityManager
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import com.nagopy.android.aplin.entity.AppEntity
import java.io.ByteArrayOutputStream
import java.util.*
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
constructor(var application: Application, var activityManager: ActivityManager) {

    /**
     * デフォルト表示用のアプリケーションアイコン
     */
    val defaultIcon: Drawable

    val defaultIconByteArray: ByteArray
    /**
     * アイコン画像の大きさ（PX)
     */
    val iconSize: Int

    val cache: Map<String, Drawable> = HashMap()

    init {
        defaultIcon = ResourcesCompat.getDrawable(application.resources, android.R.drawable.sym_def_app_icon, null)
        iconSize = activityManager.launcherLargeIconSize * 4 / 3

        defaultIconByteArray = toByteArray(defaultIcon)
    }


    open fun toByteArray(icon: Drawable): ByteArray {
        val image = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        val canvas = Canvas(image)
        icon.setBounds(0, 0, iconSize, iconSize)
        icon.draw(canvas)
        val stream = ByteArrayOutputStream()
        stream.use {
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            image.recycle()
            return stream.toByteArray()
        }
    }

    open fun toDrawable(iconArray: ByteArray): BitmapDrawable {
        return BitmapDrawable(application.resources, BitmapFactory.decodeByteArray(iconArray, 0, iconArray.size))
    }

    @Synchronized
    open fun getIcon(appEntity: AppEntity): Drawable {
        var icon = cache[appEntity.packageName]
        if (icon == null) {
            icon = toDrawable(appEntity.iconByteArray!!)
            cache.plus(Pair(appEntity.packageName, icon))
        }
        return icon
    }
}
