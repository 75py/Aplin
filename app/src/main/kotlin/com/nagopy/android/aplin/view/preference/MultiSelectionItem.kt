package com.nagopy.android.aplin.view.preference

import android.content.Context

/**
 * Created by nagopy on 2015/11/18.
 */
interface MultiSelectionItem {
    open fun getTitle(context: Context): String
    open fun getSummary(context: Context): String
    open fun minSdkVersion(): Int
    open fun maxSdkVersion(): Int
}