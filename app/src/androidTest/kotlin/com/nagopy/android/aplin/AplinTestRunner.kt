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

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.support.test.runner.AndroidJUnitRunner
import android.view.View
import android.view.WindowManager

class AplinTestRunner : AndroidJUnitRunner() {

    val wm by lazy {
        targetContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    val dummyView by lazy {
        View(targetContext.applicationContext)
    }

    val mainHandler = Handler(Looper.getMainLooper())

    override fun onStart() {
        val p = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                , PixelFormat.TRANSLUCENT)
        p.width = 0
        p.height = 0
        mainHandler.post {
            wm.addView(dummyView, p)
        }
        super.onStart()
    }

    override fun onDestroy() {
        mainHandler.post {
            wm.removeView(dummyView)
        }

        super.onDestroy()
    }

}
