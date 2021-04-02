package dev.bryanlindsey.musicgenerator3.ui.pianoroll

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.themecomposer.getShortDisplayName
import dev.bryanlindsey.themecomposer.structure.ChordTiming

@Composable
fun ChordDisplay(
    chordProgression: List<ChordTiming>,
    beatWidth: Dp,
    modifier: Modifier = Modifier,
    horizontalScrollState: ScrollState = rememberScrollState(),
    borderColor: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
        Modifier
            .horizontalScroll(horizontalScrollState)
            .then(modifier)
    ) {
        chordProgression.map {
            Box(
                modifier = Modifier
                    .width(beatWidth * it.chordLengthInBeats)
                    .fillMaxHeight()
                    .border(width = Stroke.HairlineWidth.dp, color = borderColor)
                    .background(
                        Color(red = 0.5f, green = 0.5f, blue = 0.5f, alpha = 0.125f)
                    ),
                contentAlignment = Alignment.CenterStart
            )
            {
                Text(
                    text = it.chord.getShortDisplayName(),
                    maxLines = 1,
                    modifier = Modifier
                        .absolutePadding(left = 12.dp)
                )
            }
        }
    }
}
