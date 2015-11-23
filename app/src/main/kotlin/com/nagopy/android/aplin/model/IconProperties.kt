package com.nagopy.android.aplin.model

import android.app.ActivityManager
import android.app.Application
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * アイコン表示用のプロパティを扱うクラス
 */
@Singleton
public class IconProperties
/**
 * コンストラクタ
 */
@Inject
constructor(application: Application, activityManager: ActivityManager) {

    /**
     * デフォルト表示用のアプリケーションアイコン
     */
    public val defaultIcon: Drawable
    /**
     * アイコン画像の大きさ（PX)
     */
    public val iconSize: Int

    init {
        defaultIcon = ResourcesCompat.getDrawable(application.resources, android.R.drawable.sym_def_app_icon, null)
        iconSize = activityManager.launcherLargeIconSize
    }

}
