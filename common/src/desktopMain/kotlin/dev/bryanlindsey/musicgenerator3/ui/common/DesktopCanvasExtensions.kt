package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.skija.TextLine

actual fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    textColor: Color,
    textSize: Float,
    textAlign: TextAlign
) {
    val paint = Paint()
    paint.color = textColor
    val nativePaint = paint.asFrameworkPaint()
    val font = org.jetbrains.skija.Font(null, textSize)
    drawIntoCanvas {
        val textLine = TextLine.make(text, font)
        val xOffset = when (textAlign) {
            TextAlign.Left, TextAlign.Start -> 0f
            TextAlign.Right, TextAlign.End -> textLine.width
            TextAlign.Center, TextAlign.Justify -> textLine.width / 2f
        }
        val actualX = x - xOffset

        it.nativeCanvas.drawTextLine(textLine, actualX, y, nativePaint)
    }
}