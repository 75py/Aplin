package com.nagopy.android.aplin.ui.prefs

import com.nagopy.android.aplin.R

enum class DisplayItem(val labelResId: Int) {
    FirstInstallTime(R.string.display_item_first_install_time),
    LastUpdateTime(R.string.display_item_last_update_time),
    VersionName(R.string.display_item_version_name)
    ;

    companion object {
        const val KEY = "DisplayItem"
    }
}
