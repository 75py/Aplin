package com.nagopy.android.aplin.domain.model

import android.graphics.drawable.Drawable

data class PackageModel(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val isEnabled: Boolean,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val versionName: String?
)
