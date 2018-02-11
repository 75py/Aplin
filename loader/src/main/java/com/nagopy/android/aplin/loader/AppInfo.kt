package com.nagopy.android.aplin.loader

import android.graphics.drawable.Drawable

data class AppInfo(
        val packageName: String
        , val label: String
        , val isEnabled: Boolean
        , val isDisablable: Boolean
        , val isSystem: Boolean
        , val icon: Drawable
)
