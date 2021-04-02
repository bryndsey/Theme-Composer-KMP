package dev.bryanlindsey.musicgenerator3.ui.generation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.common.DropdownList
import dev.bryanlindsey.musicgenerator3.ui.theme.MusicGenerator3Theme
import dev.bryanlindsey.themecomposer.structure.Chord
import dev.bryanlindsey.themecomposer.structure.ChordTiming
import dev.bryanlindsey.themecomposer.structure.ChordType
import dev.bryanlindsey.themecomposer.structure.NamedPitch

private const val MAX_CHORD_IN_PROGRESSION = 6

@Composable
fun ChordProgressionInput(
    chordProgression: List<ChordTiming>,
    onUpdateProgression: (newProgression: List<ChordTiming>) -> Unit
) {

    // TODO: Add drag and drop support
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            val canDeleteChords = chordProgression.size > 1
            chordProgression.mapIndexed { index, item ->

                ChordProgressionInputItem(
                    chord = item.chord,
                    length = item.chordLengthInBeats,
                    onChordChanged = { newChord ->
                        val newProgression = chordProgression.toMutableList()
                        val newChordWithLength = newProgression[index].copy(chord = newChord)
                        newProgression[index] = newChordWithLength
                        onUpdateProgression(newProgression)
                    },
                    onChordLengthChanged = { newLength ->
                        val newProgression = chordProgression.toMutableList()
                        val newChordWithLength =
                            newProgression[index].copy(chordLengthInBeats = newLength)
                        newProgression[index] = newChordWithLength
                        onUpdateProgression(newProgression)
                    },
                    onChordRemoved = {
                        val newProgression = chordProgression.toMutableList()
                        newProgression.removeAt(index)
                        onUpdateProgression(newProgression)
                                     },
                    canRemoveChord = canDeleteChords
                )
            }

            if (chordProgression.size < MAX_CHORD_IN_PROGRESSION) {
                IconButton(
                    onClick = {
                        val newProgression = chordProgression +
                                ChordTiming(Chord(NamedPitch.C, ChordType.MAJOR), 4)
                        onUpdateProgression(newProgression)
                    },
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add chord")
                }
            }
        }
    )
}

@Composable
fun ChordProgressionInputItem(
    chord: Chord,
    length: Int,
    onChordChanged: (Chord) -> Unit,
    onChordLengthChanged: (Int) -> Unit,
    onChordRemoved: () -> Unit,
    canRemoveChord: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChordSelectionControl(
            chordRootPitch = chord.rootPitch,
            onChordRootPitchChanged = { newPitch ->
                val newChord = chord.copy(rootPitch = newPitch)
                onChordChanged(newChord)
            },
            chordType = chord.type,
            onChordTypeChanged = { newChordType ->
                val newChord = chord.copy(type = newChordType)
                onChordChanged(newChord)
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        DropdownList(
            listItems = listOf(1, 2, 3, 4, 5, 6, 7, 8),
            onItemSelected = { newLength ->
                onChordLengthChanged(newLength)
            },
            selectedItem = length,
            dropdownHeaderText = "Number of beats",
            modifier = Modifier.width(64.dp),
        )

        IconButton(
            onClick = onChordRemoved,
            enabled = canRemoveChord,
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Chord")
        }
    }
}

//@Preview
//@Composable
//fun ChordProgressionControlPreview() {
//    val testProgression = listOf(
//        ChordTiming(Chord(NamedPitch.F, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.G, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.C, ChordType.MAJOR), 4),
//        ChordTiming(Chord(NamedPitch.C, ChordType.MAJOR), 4),
//    )
//
//    val currentProgression = remember { mutableStateOf(testProgression) }
//
//    MusicGenerator3Theme {
//        ChordProgressionInput(
//            chordProgression = currentProgression.value,
//            onUpdateProgression = { currentProgression.value = it })
//    }
//}
