package com.nagopy.android.aplin.model.preference

import com.nagopy.android.aplin.R
import com.nagopy.android.aplin.model.Category
import com.nagopy.android.easyprefs.EasyPref
import com.nagopy.android.easyprefs.annotations.EasyPrefMultiSelection

@EasyPrefMultiSelection(
        target = Category::class
        , title = R.string.category
        , defValue = "ALL,SYSTEM,USER,DISABLED"
        , nullable = false
)
public interface CategorySetting : EasyPref<java.util.List<Category>>
