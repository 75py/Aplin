package com.pluscubed.recyclerfastscroll

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet

class AplinRecyclerFastScroller : com.pluscubed.recyclerfastscroll.RecyclerFastScroller {

    constructor(context: Context?) : super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun attachAppBarLayout(coordinatorLayout: CoordinatorLayout?, appBarLayout: AppBarLayout?) {
        this.mCoordinatorLayout = coordinatorLayout
        this.mAppBarLayout = appBarLayout

        appBarLayout?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            show(true)

            val layoutParams = layoutParams as MarginLayoutParams
            // ここ、topMarginじゃなくてbottomMarginでは？
            layoutParams.bottomMargin = mAppBarLayout.height + verticalOffset; //AppBarLayout actual height

            mAppBarLayoutOffset = -verticalOffset;

            setLayoutParams(layoutParams);
        }
    }
}
