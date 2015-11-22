package com.nagopy.android.aplin.view.preference

import android.content.Context

interface SingleSelectionItem {
    open fun getTitle(context: Context): String
    open fun getSummary(context: Context): String
    open fun minSdkVersion(): Int
    open fun maxSdkVersion(): Int
}