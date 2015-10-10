package com.nagopy.android.aplin.view.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView

public class DividerItemDecoration(context: Context, colorResId: Int, dividerWidthDimenResId: Int) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.color = ContextCompat.getColor(context, colorResId)
        mPaint.strokeWidth = context.resources.getDimension(dividerWidthDimenResId).toInt().toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val startX = parent.paddingLeft
        val stopX = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val y = child.bottom + params.bottomMargin

            c.drawLine(startX.toFloat(), y.toFloat(), stopX.toFloat(), y.toFloat(), mPaint)
        }
    }
}
