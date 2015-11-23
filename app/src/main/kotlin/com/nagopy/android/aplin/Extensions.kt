package com.nagopy.android.aplin

import android.view.View

fun <E : View> E.visible(): Unit {
    visibility = View.VISIBLE
}

fun <E : View> E.gone(): Unit {
    visibility = View.GONE
}
