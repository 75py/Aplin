package com.nagopy.android.aplin

import com.nagopy.android.aplin.loader.AppInfo

enum class Category(val titleResId: Int, val predicate: (AppInfo) -> Boolean) {

    ALL(R.string.category_all, { true }),

    SYSTEM(R.string.category_system, AppInfo::isSystem),

    DISABLABLE(R.string.category_disablable, { it.isDisablable && it.isEnabled }),

    DISABLED(R.string.category_disabled, { !it.isEnabled }),

    UNDISABLABLE(R.string.category_undisablable, { it.isEnabled && !it.isDisablable }),

    USER(R.string.category_user, { it.isSystem });

}
