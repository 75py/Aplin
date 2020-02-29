package com.nagopy.android.aplin

import androidx.test.platform.app.InstrumentationRegistry
import com.nagopy.android.aplin.test.R

object TestResources {

    object string {
        val test_btn_disable = InstrumentationRegistry.getContext().getString(R.string.test_btn_disable)!!

        val test_btn_enable = InstrumentationRegistry.getContext().getString(R.string.test_btn_enable)!!

        val test_permissions = InstrumentationRegistry.getContext().getString(R.string.test_permissions)!!
    }
}