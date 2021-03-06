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
package com.nagopy.android.aplin.constants

import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 定数クラス
 */
object Constants {

    /**
     * 改行
     */
    val LINE_SEPARATOR: String = System.getProperty("line.separator") ?: "\n"

    /**
     * 2000/01/01（ミリ秒）
     */
    val Y2K: Long

    init {
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        try {
            @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            Y2K = simpleDateFormat.parse("20000101").time
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }

    val ALL_SDK_VERSION = IntRange(Build.VERSION_CODES.BASE, Int.MAX_VALUE)


    const val MIME_TYPE_TEXT_PLAIN = "text/plain"
}
