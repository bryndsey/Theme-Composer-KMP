package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    textColor: Color,
    textSize: Float = 16.dp.toPx(),
    textAlign: TextAlign = TextAlign.Left
) {
    val paint = Paint()
    paint.color = textColor
    val nativePaint = paint.asFrameworkPaint()
    nativePaint.textSize = textSize

    val nativeAlign = when(textAlign) {
        TextAlign.Left, TextAlign.Start -> android.graphics.Paint.Align.LEFT
        TextAlign.Right,TextAlign.End -> android.graphics.Paint.Align.RIGHT
        TextAlign.Center, TextAlign.Justify -> android.graphics.Paint.Align.CENTER
    }
    nativePaint.textAlign = nativeAlign

    drawIntoCanvas {
        it.nativeCanvas.drawText(text, x, y, nativePaint)
    }
}
