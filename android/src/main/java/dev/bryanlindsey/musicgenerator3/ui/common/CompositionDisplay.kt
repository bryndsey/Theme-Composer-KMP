package dev.bryanlindsey.musicgenerator3.ui.common

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.pianoroll.PianoRoll
import dev.bryanlindsey.themecomposer.Composition
import dev.bryanlindsey.themecomposer.getSemitoneOffsetFromMiddleC
import dev.bryanlindsey.themecomposer.structure.ALL_SPECIFIC_NOTES
import dev.bryanlindsey.themecomposer.structure.MIDDLE_C
import dev.bryanlindsey.themecomposer.structure.Note
import dev.bryanlindsey.themecomposer.structure.SpecificPitch

private const val MIN_PIANO_KEYS = 14

@Composable
fun CompositionDisplay(
    modifier: Modifier = Modifier,
    composition: Composition? = null,
    name: String? = null,
) {
    val orientation =
        if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) {
            Orientation.Horizontal
        } else Orientation.Vertical

    val noteRange = getPitchRange(composition?.melody ?: emptyList())

    if (orientation == Orientation.Vertical) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionProperties(
                composition = composition,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                name = name,
            )

            Divider()

            PianoRoll(
                noteColor = MaterialTheme.colors.secondary,
                noteList = composition?.melody ?: emptyList(),
                chordProgression = composition?.chordProgression ?: emptyList(),
                beatsPerMeasure = composition?.timeSignature?.beatsPerMeasure ?: 4,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                lowestNoteShown = noteRange.first(),
                highestNoteShown = noteRange.last(),
                minWidthAfterLastNote = 128.dp,
            )
        }
    } else {
        Row(modifier = modifier) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.25f),
                elevation = 4.dp,
            ) {
                val scrollState = rememberScrollState()

                CompositionProperties(
                    composition = composition,
                    modifier = Modifier
                        .verticalScroll(
                            state = scrollState,
                        )
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    name = name,
                    orientation = Orientation.Vertical,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                // It looks pretty awkward without an extra space
                Spacer(modifier = Modifier.height(20.dp))
                Divider()

                PianoRoll(
                    noteColor = MaterialTheme.colors.secondary,
                    noteList = composition?.melody ?: emptyList(),
                    chordProgression = composition?.chordProgression ?: emptyList(),
                    beatsPerMeasure = composition?.timeSignature?.beatsPerMeasure ?: 4,
                    modifier = Modifier.fillMaxSize(),
                    lowestNoteShown = noteRange.first(),
                    highestNoteShown = noteRange.last(),
                    minWidthAfterLastNote = 128.dp,
                )
            }
        }
    }
}

fun getPitchRange(
    notes: List<Note>,
    minRangeSemitones: Int = MIN_PIANO_KEYS
) : List<SpecificPitch> {
    val minPitch = notes.minByOrNull { it.pitch.getSemitoneOffsetFromMiddleC() }?.pitch ?: MIDDLE_C
    val maxPitch = notes.maxByOrNull { it.pitch.getSemitoneOffsetFromMiddleC() }?.pitch ?: MIDDLE_C

    val allNotes = ALL_SPECIFIC_NOTES
    var minPitchIndex = allNotes.indexOf(minPitch)
    var maxPitchIndex = allNotes.indexOf(maxPitch)

    var rangeSize = maxPitchIndex - minPitchIndex + 1
    // Using a while loop to better account for the max size of the pitch list
    while (rangeSize < minRangeSemitones) {
        maxPitchIndex = minOf(maxPitchIndex + 1, ALL_SPECIFIC_NOTES.lastIndex)
        rangeSize = maxPitchIndex - minPitchIndex + 1

        // if we're already at the minimum, no need to change the min value
        if (rangeSize >= MIN_PIANO_KEYS) break

        minPitchIndex = maxOf(minPitchIndex - 1, 0)
        rangeSize = maxPitchIndex - minPitchIndex + 1
    }

    return allNotes.subList(minPitchIndex, maxPitchIndex + 1)
}
