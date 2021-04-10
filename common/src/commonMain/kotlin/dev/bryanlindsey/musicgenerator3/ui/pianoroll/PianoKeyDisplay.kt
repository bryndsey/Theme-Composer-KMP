package dev.bryanlindsey.musicgenerator3.ui.pianoroll

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bryanlindsey.musicgenerator3.ui.common.drawText
import dev.bryanlindsey.themecomposer.getDisplayName
import dev.bryanlindsey.themecomposer.structure.ALL_SPECIFIC_NOTES
import dev.bryanlindsey.themecomposer.structure.NamedPitch
import dev.bryanlindsey.themecomposer.structure.SpecificPitch
import kotlin.math.min

val BLACK_KEYS = setOf(
    NamedPitch.A_SHARP_B_FLAT,
    NamedPitch.C_SHARP_D_FLAT,
    NamedPitch.D_SHARP_E_FLAT,
    NamedPitch.F_SHARP_G_FLAT,
    NamedPitch.G_SHARP_A_FLAT,
)

@Composable
fun PianoKeyDisplay(
    modifier: Modifier = Modifier,
    lowestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.first(),
    highestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.last(),
    verticalScrollState: ScrollState = rememberScrollState()
) {
    Canvas(
        modifier = Modifier
            .verticalScroll(verticalScrollState)
            .then(modifier)
    ) {

        val allNotes = ALL_SPECIFIC_NOTES
        val minPitchIndex = allNotes.indexOf(lowestNoteShown)
        val maxPitchIndex = allNotes.indexOf(highestNoteShown)

        val notesInRange = allNotes.subList(minPitchIndex, maxPitchIndex + 1)
        val noteHeight = size.height / notesInRange.size

        translate(top = size.height) {
            // Draw piano keys
            notesInRange.mapIndexed { index, pitch ->
                val pianoKeyColor = if (BLACK_KEYS.contains(pitch.namedPitch)) {
                    Color.Black
                } else {
                    Color.White
                }

                val noteTopY = -((index + 1) * noteHeight)

                drawRect(
                    color = pianoKeyColor,
                    topLeft = Offset(x = 0f, y = noteTopY),
                    size = Size(
                        width = size.width,
                        height = noteHeight
                    )
                )

                val pianoNoteNamePadding = 4.dp
                val textSize = min(noteHeight - (2f * pianoNoteNamePadding.toPx()), 16.dp.toPx())

                val textBaselineOffset = 2.sp.toPx()
                val textTopY = noteTopY + (noteHeight / 2f) + (textSize / 2f) - textBaselineOffset
                drawText(
                    pitch.namedPitch.getDisplayName() + (pitch.octave + 1),// + 'j',
                    size.width / 2f,
                    textTopY,
                    textColor = Color.Gray,
                    textSize = textSize,
                    textAlign = TextAlign.Center
                )

                // Debugging lines
//                    val midlineHeight = noteTopY + (noteHeight / 2)
//                    drawLine(
//                        color = Color.Red,
//                        start = Offset(0f, midlineHeight),
//                        end = Offset(size.width, midlineHeight)
//                    )
//                    drawLine(
//                        color = Color.Red,
//                        start = Offset(0f, midlineHeight + (textSize/2)),
//                        end = Offset(size.width, midlineHeight + (textSize/2))
//                    )
//                    drawLine(
//                        color = Color.Red,
//                        start = Offset(0f, midlineHeight - (textSize/2)),
//                        end = Offset(size.width, midlineHeight - (textSize/2))
//                    )

                val strokeWidth = 1.dp

                drawRect(
                    color = Color.Black,
                    topLeft = Offset(x = 0f, y = noteTopY),
                    size = Size(
                        width = size.width - (strokeWidth / 2).toPx(),
                        height = noteHeight
                    ),
                    style = Stroke(width = strokeWidth.toPx())
                )
            }
        }
    }
}
