package dev.bryanlindsey.musicgenerator3.ui.pianoroll

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.common.drawText

@Composable
fun MeasureDisplay(
    beatsPerMeasure: Int,
    beatWidth: Dp,
    modifier: Modifier = Modifier,
    horizontalScrollState: ScrollState = rememberScrollState(),
    contentColor: Color = LocalContentColor.current
) {

    Canvas(
        modifier =
        Modifier
            .horizontalScroll(horizontalScrollState)
            .then(modifier)
    ) {

        // Draw underline
        drawLine(
            color = contentColor,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height)
        )

        val measureWidth = beatWidth * beatsPerMeasure

        val measureCount = (size.width / measureWidth.toPx()).toInt()

        (0..measureCount).map { measureNumber ->

            val measureStartX = measureWidth * measureNumber

            val textPadding = 4.dp
            val textSize = size.height - (textPadding * 2).toPx()

            // Draw measure number
            drawText(
                (measureNumber + 1).toString(),
                (measureStartX.toPx()) + textPadding.toPx(),
                y = size.height - textPadding.toPx(),
                textColor = contentColor,
                textSize = textSize,
            )

            // Measure start line
            drawLine(
                color = contentColor,
                start = Offset(measureStartX.toPx(), 0f),
                end = Offset(measureStartX.toPx(), size.height),
            )

            //Beat lines
            (1 until beatsPerMeasure).onEach { beatIndex ->
                val beatStartX = measureStartX + (beatWidth * beatIndex)
                drawLine(
                    color = contentColor,
                    start = Offset(beatStartX.toPx(), size.height),
                    end = Offset(beatStartX.toPx(), size.height * (0.66f)),
                )
            }
        }
    }
}
