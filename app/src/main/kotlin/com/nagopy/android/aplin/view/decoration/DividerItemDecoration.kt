/*
 * Copyright 2015 75py
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
