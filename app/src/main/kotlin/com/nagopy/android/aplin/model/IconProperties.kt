package com.nagopy.android.aplin.model

import android.app.ActivityManager
import android.app.Application
import android.content.Context
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
constructor(application: Application) {

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
        iconSize = getIconSize(application)
    }

    companion object {

        /**
         * ランチャーで表示するアイコンのサイズを取得する.

         * @param context Context
         * *
         * @return ランチャーのアイコンサイズ
         */
        fun getIconSize(context: Context): Int {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return activityManager.launcherLargeIconSize
            /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            return activityManager.getLauncherLargeIconSize();
        } else {
            return (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
        }
        */
        }
    }
}
