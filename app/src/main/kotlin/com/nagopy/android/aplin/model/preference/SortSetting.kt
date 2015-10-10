package com.nagopy.android.aplin.model.preference

import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Sort
import com.nagopy.android.easyprefs.EasyPref
import com.nagopy.android.easyprefs.annotations.EasyPrefSingleSelection

@EasyPrefSingleSelection(
        target = Sort::class
        , title = R.string.sort
        , defValue = "DEFAULT"
        , nullable = false
)
public interface SortSetting : EasyPref<Sort>
