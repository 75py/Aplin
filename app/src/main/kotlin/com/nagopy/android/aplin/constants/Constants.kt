/*
 * Copyright (C) 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagopy.android.aplin.constants

import android.app.ActivityManager
import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 定数クラス
 */
public object Constants {

    /**
     * 改行
     */
    public val LINE_SEPARATOR: String = System.getProperty("line.separator")

    /**
     * 2000/01/01（ミリ秒）
     */
    public val Y2K: Long

    init {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        try {
            Y2K = simpleDateFormat.parse("20000101").time
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 実行ステータス
     */
    public val RUNNING_STATUS: Map<Int, String>

    init {
        val runningStatusMap = HashMap<Int, String>()
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND, "Background")
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND, "Foreground")
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE, "Perceptible")
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE, "Service")
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE, "Visible")
        runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_EMPTY, "Empty")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            runningStatusMap.put(ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE, "Gone")
        }
        RUNNING_STATUS = Collections.unmodifiableMap(runningStatusMap)
    }

    public val ALL_SDK_VERSION = IntRange(Build.VERSION_CODES.BASE, Int.MAX_VALUE)

}
