package dev.bryanlindsey.musicgenerator3.ui.pianoroll

import androidx.compose.foundation.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.theme.pianoNoteBackgroundDark
import dev.bryanlindsey.musicgenerator3.ui.theme.pianoNoteBackgroundLight
import dev.bryanlindsey.themecomposer.getSemitoneOffsetFromMiddleC
import dev.bryanlindsey.themecomposer.structure.ALL_SPECIFIC_NOTES
import dev.bryanlindsey.themecomposer.structure.Note
import dev.bryanlindsey.themecomposer.structure.SpecificPitch


@Composable
fun NoteAreaDisplay(
    noteList: List<Note>,
    noteColor: Color,
    beatsPerMeasure: Int,
    beatWidth: Dp,
    modifier: Modifier = Modifier,
    lowestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.first(),
    highestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.last(),
    verticalScrollState: ScrollState = rememberScrollState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    widthBeforeFirstNote: Dp = 0.dp,
) {

    val noteTrackColor1 = if (MaterialTheme.colors.isLight) {
        pianoNoteBackgroundLight
    } else {
        pianoNoteBackgroundDark
    }
    val noteTrackColor2 =
        Color(0f, 0f, 0f, 0.05f).compositeOver(noteTrackColor1)

    val pitchRangeSize = highestNoteShown.getSemitoneOffsetFromMiddleC() - lowestNoteShown.getSemitoneOffsetFromMiddleC() + 1

    Canvas(
        modifier = Modifier
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
            .then(modifier)
    ) {
        drawPitchLanes(
            numberOfLanes = pitchRangeSize,
            noteTrackColor1 = noteTrackColor1,
            noteTrackColor2 = noteTrackColor2
        )

        drawMeasureLines(
            beatsPerMeasure = beatsPerMeasure,
            beatWidth = beatWidth,
            paddingBeforeFirstMeasureLine = widthBeforeFirstNote
        )

        drawOverlayBeforeFirstMeasure(
            widthBeforeFirstNote
        )

        drawNotes(
            noteList = noteList,
            noteColor = noteColor,
            beatWidth = beatWidth,
            paddingBeforeFirstMeasureLine = widthBeforeFirstNote,
            lowestNoteShown = lowestNoteShown,
            highestNoteShown = highestNoteShown
        )
    }
}

private fun DrawScope.drawPitchLanes(
    numberOfLanes: Int,
    noteTrackColor1: Color,
    noteTrackColor2: Color,
) {
    val noteHeight = size.height / numberOfLanes

    // Draw note "tracks"
    (0..numberOfLanes).map {
        val barColor = if (it % 2 == 0) {
            noteTrackColor1
        } else {
            noteTrackColor2
        }

        drawRect(
            color = barColor,
            topLeft = Offset(x = 0f, y = it * noteHeight),
            size = Size(
                width = size.width,
                height = noteHeight
            )
        )
    }
}

private fun DrawScope.drawMeasureLines(
    beatsPerMeasure: Int,
    beatWidth: Dp,
    paddingBeforeFirstMeasureLine: Dp
) {
    // This assumes a subbeat is half of a beat. This may not hold true long-term
    val distanceBetweenMeasureLines = beatWidth * beatsPerMeasure
    val numberOfMeasures = ((size.width.toDp() - paddingBeforeFirstMeasureLine) / distanceBetweenMeasureLines).toInt()

    translate(left = paddingBeforeFirstMeasureLine.toPx()) {
        (0..numberOfMeasures).map {
            val barLineX = distanceBetweenMeasureLines * it

            drawLine(
                color = Color.Gray,
                start = Offset(barLineX.toPx(), 0f),
                end = Offset(barLineX.toPx(), size.height),
                alpha = 0.5f
            )
        }
    }
}

private fun DrawScope.drawOverlayBeforeFirstMeasure(
    paddingBeforeFirstNote: Dp
) {
    val paddingOverlayColor = Color(0f, 0f, 0f, 0.2f)

    drawRect(
        color = paddingOverlayColor,
        topLeft = Offset(x = 0f, 0f),
        size = Size(
            width = paddingBeforeFirstNote.toPx(),
            height = size.height
        )
    )
}

private fun DrawScope.drawNotes(
    noteList: List<Note>,
    noteColor: Color,
    beatWidth: Dp,
    paddingBeforeFirstMeasureLine: Dp,
    lowestNoteShown: SpecificPitch,
    highestNoteShown: SpecificPitch
) {
    val minNoteSemitoneOffset = lowestNoteShown.getSemitoneOffsetFromMiddleC()
    val pitchRangeSize = highestNoteShown.getSemitoneOffsetFromMiddleC() - minNoteSemitoneOffset + 1
    val noteHeight = size.height / pitchRangeSize

    translate(left = paddingBeforeFirstMeasureLine.toPx(), top = size.height) {
        // I think I'm going to ditch this for now.
        // The implementation gets weirder after extracting this function, and I'm not
        // sure if the value provided is worth the code ugliness

//                //Draw overlay for after last note/chord end
//                drawRect(
//                    color = noteTrackOverlay,
//                    topLeft = Offset(x = contentEndPosition.toPx(), -size.height),
//                    size = Size(
//                        width = size.width - contentEndPosition.toPx(),
//                        height = size.height
//                    )
//                )

        val subbeatWidth = beatWidth / 2

        noteList.onEach {
            val noteStartX =
                it.noteTiming.noteStartEighthNote * subbeatWidth.toPx()
            val noteWidth =
                it.noteTiming.noteLengthInEightNotes * subbeatWidth.toPx()

            val noteSemitoneOffset =
                it.pitch.getSemitoneOffsetFromMiddleC() - minNoteSemitoneOffset
            val noteStartTopY =
                -(noteHeight * (noteSemitoneOffset + 1))
            drawRoundRect(
                color = noteColor,
                cornerRadius = CornerRadius(4.dp.toPx()),
                topLeft = Offset(noteStartX, noteStartTopY),
                size = Size(noteWidth - 1.dp.toPx(), noteHeight)
            )
        }
    }
}
