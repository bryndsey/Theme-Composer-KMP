package dev.bryanlindsey.musicgenerator3.ui.pianoroll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import dev.bryanlindsey.musicgenerator3.ui.theme.MusicGenerator3Theme
import dev.bryanlindsey.themecomposer.getSemitoneOffsetFromMiddleC
import dev.bryanlindsey.themecomposer.structure.*
import kotlin.math.max

@Composable
fun PianoRoll(
    noteColor: Color,
    noteList: List<Note>,
    beatsPerMeasure: Int,
    modifier: Modifier = Modifier,
    lowestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.first(),
    highestNoteShown: SpecificPitch = ALL_SPECIFIC_NOTES.last(),
    chordProgression: List<ChordTiming>? = null,
    beatWidth: Dp = 48.dp,
    minNoteHeight: Dp = 24.dp,
    paddingBeforeFirstNote: Dp = 20.dp,
    minWidthAfterLastNote: Dp = beatWidth,
    pianoKeyWidth: Dp = 64.dp,
    measureDisplayHeight: Dp = 24.dp,
    chordDisplayHeight: Dp = 32.dp,
) {
    BoxWithConstraints(modifier = modifier) {

        val showChords = !chordProgression.isNullOrEmpty() && chordDisplayHeight > 0.dp

        val actualChordHeight = chordDisplayHeight.takeIf { showChords } ?: 0.dp

        val maxNoteAreaScreenWidth = maxWidth - pianoKeyWidth
        val maxNoteAreaScreenHeight = maxHeight - measureDisplayHeight - actualChordHeight

        val (canvasWidth, canvasHeight) = calculateNoteAreaCanvasSize(
            maxScreenAreaWidth = maxNoteAreaScreenWidth,
            maxScreenAreaHeight = maxNoteAreaScreenHeight,
            noteList = noteList,
            chordProgression = chordProgression,
            lowestNoteShown = lowestNoteShown,
            highestNoteShown = highestNoteShown,
            beatWidth = beatWidth,
            minNoteHeight = minNoteHeight,
            paddingBeforeFirstNote = paddingBeforeFirstNote,
            minWidthAfterLastNote = minWidthAfterLastNote,
        )

        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()
        Column {
            Row {
                Spacer(modifier = Modifier.width(pianoKeyWidth))
                Column {

                    val color = LocalContentColor.current

                    MeasureDisplay(beatsPerMeasure = beatsPerMeasure, beatWidth = beatWidth,
                        modifier = Modifier
                            .height(measureDisplayHeight)
                            .width(canvasWidth)
                            .absolutePadding(left = paddingBeforeFirstNote),
                        horizontalScrollState = horizontalScrollState,
                        contentColor = color,
                    )

                    if (showChords) {
                        ChordDisplay(
                            chordProgression = chordProgression ?: emptyList(),
                            beatWidth = beatWidth,
                            modifier = Modifier
                                .height(chordDisplayHeight)
                                .width(canvasWidth)
                                .absolutePadding(left = paddingBeforeFirstNote),
                            horizontalScrollState = horizontalScrollState,
                            borderColor = color,
                        )
                    }
                }
            }

            Row {
                PianoKeyDisplay(
                    modifier = Modifier
                        .height(canvasHeight)
                        .width(pianoKeyWidth),
                    verticalScrollState = verticalScrollState,
                    lowestNoteShown = lowestNoteShown,
                    highestNoteShown = highestNoteShown,
                )

                NoteAreaDisplay(
                    noteList = noteList,
                    noteColor = noteColor,
                    beatsPerMeasure = beatsPerMeasure,
                    beatWidth = beatWidth,
                    modifier = Modifier
                        .height(canvasHeight)
                        .width(canvasWidth),
                    lowestNoteShown = lowestNoteShown,
                    highestNoteShown = highestNoteShown,
                    verticalScrollState = verticalScrollState,
                    horizontalScrollState = horizontalScrollState,
                    widthBeforeFirstNote = paddingBeforeFirstNote,
                )
            }
        }
    }
}

private fun calculateNoteAreaCanvasSize(
    maxScreenAreaWidth: Dp,
    maxScreenAreaHeight: Dp,
    noteList: List<Note>,
    chordProgression: List<ChordTiming>?,
    lowestNoteShown: SpecificPitch,
    highestNoteShown: SpecificPitch,
    beatWidth: Dp,
    minNoteHeight: Dp,
    paddingBeforeFirstNote: Dp,
    minWidthAfterLastNote: Dp
): Pair<Dp, Dp> {
    val minNoteSemitoneOffset = lowestNoteShown.getSemitoneOffsetFromMiddleC()
    val noteRangeSize = highestNoteShown.getSemitoneOffsetFromMiddleC() - minNoteSemitoneOffset + 1
    val lastNoteEnd =
        noteList.maxOfOrNull { it.noteTiming.noteStartEighthNote + it.noteTiming.noteLengthInEightNotes }
            ?: 0

    val lastChordEndInSubbeats = (chordProgression?.sumOf { it.chordLengthInBeats } ?: 0) * 2

    val contentEndPosition = (beatWidth / 2) * max(lastNoteEnd, lastChordEndInSubbeats)

    val canvasWidth =
        max(contentEndPosition + paddingBeforeFirstNote + minWidthAfterLastNote, maxScreenAreaWidth)
    val canvasHeight = max(minNoteHeight * noteRangeSize, maxScreenAreaHeight)

    return canvasWidth to canvasHeight
}


//@Preview
//@Composable
//fun PianoRollPreview() {
//
//    val minNote = ALL_SPECIFIC_NOTES.first()
//    val maxNote = ALL_SPECIFIC_NOTES.last()
//
//    val noteList = listOf(
//        Note(SpecificPitch(NamedPitch.F, 4), NoteTiming(0, 6)),
//        Note(minNote, NoteTiming(4, 4)),
//        Note(maxNote, NoteTiming(8, 4)),
//        Note(maxNote, NoteTiming(12, 4)),
//        Note(SpecificPitch(NamedPitch.E, 4), NoteTiming(8, 6)),
//        Note(SpecificPitch(NamedPitch.C, 4), NoteTiming(8, 6)),
//        Note(SpecificPitch(NamedPitch.G, 4), NoteTiming(8, 6)),
//        Note(SpecificPitch(NamedPitch.C, 5), NoteTiming(14, 2)),
//        Note(SpecificPitch(NamedPitch.G, 4), NoteTiming(16, 4)),
//        Note(SpecificPitch(NamedPitch.D, 4), NoteTiming(20, 4)),
//        Note(SpecificPitch(NamedPitch.E, 4), NoteTiming(24, 8)),
//        Note(SpecificPitch(NamedPitch.C, 4), NoteTiming(24, 8)),
//        Note(SpecificPitch(NamedPitch.G, 4), NoteTiming(24, 8)),
//    )
//
//    val chordProgression = listOf(
//        ChordTiming(Chord(NamedPitch.F, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.C, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.G, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.C, ChordType.MAJOR), 4),
//    )
//
////    val noteList = listOf(Note(pitch=SpecificPitch(namedPitch= NamedPitch.A, octave=3), noteTiming=NoteTiming(noteStartEighthNote=0, noteLengthInEightNotes=5)), Note(pitch=SpecificPitch(namedPitch=NamedPitch.B, octave=3), noteTiming=NoteTiming(noteStartEighthNote=5, noteLengthInEightNotes=4)), Note(pitch=SpecificPitch(namedPitch= NamedPitch.A, octave=3), noteTiming=NoteTiming(noteStartEighthNote=9, noteLengthInEightNotes=3)))
////    val chordProgression = listOf(ChordTiming(chord=Chord(rootPitch=NamedPitch.A, type= ChordType.MAJOR), chordLengthInBeats=6))
//
//    MusicGenerator3Theme(
////        darkTheme = true
//    ) {
//        Surface(Modifier.fillMaxSize()) {
//            PianoRoll(
//                noteColor = MaterialTheme.colors.secondary,
//                noteList = noteList,
//                lowestNoteShown = minNote,
//                highestNoteShown = maxNote,
//                chordProgression = chordProgression,
//                beatsPerMeasure = 4,
//                modifier = Modifier
//                    .fillMaxSize(),
//            )
//        }
//    }
//}
