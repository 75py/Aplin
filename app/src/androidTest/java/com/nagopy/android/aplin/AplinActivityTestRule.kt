package com.nagopy.android.aplin

import android.app.Activity
import android.graphics.PixelFormat
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.WindowManager

class AplinActivityTestRule<T : Activity> : ActivityTestRule<T> {

    lateinit var view: View
    lateinit var windowManager: WindowManager

    constructor(cls: Class<T>) : super(cls)
    constructor(cls: Class<T>, initialTouchMode: Boolean) : super(cls, initialTouchMode)
    constructor(cls: Class<T>, initialTouchMode: Boolean, launchActivity: Boolean) : super(cls, initialTouchMode, launchActivity)

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        runOnUiThread {
            windowManager = activity.windowManager
            view = View(activity)
            windowManager.addView(view, WindowManager.LayoutParams(0, 0
                    , WindowManager.LayoutParams.TYPE_APPLICATION
                    , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    , PixelFormat.TRANSLUCENT))
        }
    }

    override fun finishActivity() {
        runOnUiThread {
            windowManager.removeViewImmediate(view)
        }
        super.finishActivity()
    }

}
