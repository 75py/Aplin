package com.nagopy.android.aplin

import com.nagopy.android.aplin.loader.AppInfo

enum class Category(val predicate: (AppInfo) -> Boolean) {

    ALL({ true }),

    SYSTEM(AppInfo::isSystem),

    DISABLABLE({ it.isDisablable && it.isEnabled }),

    DISABLED({ !it.isEnabled }),

    UNDISABLABLE({ it.isEnabled && !it.isDisablable }),

    USER({ it.isSystem });

}
