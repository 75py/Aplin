package com.nagopy.android.aplin.model.preference


import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.DisplayItem
import com.nagopy.android.easyprefs.EasyPref
import com.nagopy.android.easyprefs.annotations.EasyPrefMultiSelection

@EasyPrefMultiSelection(
        target = DisplayItem::class
        , title = R.string.display_item
        , defValue = "PROCESS"
        , nullable = true
)
public interface DisplayItemSetting : EasyPref<java.util.List<DisplayItem>>
