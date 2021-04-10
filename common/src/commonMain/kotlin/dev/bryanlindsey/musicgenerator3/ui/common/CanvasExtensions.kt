package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

expect fun DrawScope.drawText(
    text: String,
    x: Float,
    y: Float,
    textColor: Color,
    textSize: Float,
    textAlign: TextAlign
)
