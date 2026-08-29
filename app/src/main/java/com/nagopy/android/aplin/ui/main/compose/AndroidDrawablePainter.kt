package com.nagopy.android.aplin.ui.main.compose

import android.graphics.drawable.Drawable
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Composable
internal fun rememberAndroidDrawablePainter(drawable: Drawable): Painter = remember(drawable) { AndroidDrawablePainter(drawable) }

private class AndroidDrawablePainter(
    private val drawable: Drawable,
) : Painter() {
    override val intrinsicSize: Size
        get() =
            if (drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                Size(drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            } else {
                Size.Unspecified
            }

    override fun DrawScope.onDraw() {
        drawable.setLayoutDirection(
            if (layoutDirection == LayoutDirection.Rtl) {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            },
        )
        drawable.setBounds(
            0,
            0,
            size.width.roundToInt().coerceAtLeast(0),
            size.height.roundToInt().coerceAtLeast(0),
        )
        drawIntoCanvas { canvas ->
            canvas.save()
            try {
                drawable.draw(canvas.nativeCanvas)
            } finally {
                canvas.restore()
            }
        }
    }
}
