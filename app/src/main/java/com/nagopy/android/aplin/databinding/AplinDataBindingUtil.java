package com.nagopy.android.aplin.databinding;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

public class AplinDataBindingUtil {

    private AplinDataBindingUtil() {
        throw new AssertionError();
    }

    /**
     * @see DataBindingUtil#setContentView(Activity, int)
     */
    public static <T extends ViewDataBinding> T setContentView(Activity activity, int layoutId) {
        return DataBindingUtil.setContentView(activity, layoutId);
    }
}
