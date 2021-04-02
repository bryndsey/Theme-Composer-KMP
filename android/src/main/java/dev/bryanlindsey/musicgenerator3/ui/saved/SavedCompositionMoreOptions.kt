package dev.bryanlindsey.musicgenerator3.ui.saved

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.bryanlindsey.musicgenerator3.SavedComposition
import dev.bryanlindsey.musicgenerator3.player.Voice
import dev.bryanlindsey.musicgenerator3.player.VolumeControl
import dev.bryanlindsey.musicgenerator3.ui.main.VoiceVolumeControls

@Composable
fun SavedCompositionMoreOptions(
    savedComposition: SavedComposition?,
    volumeControl: VolumeControl,
    onExportClicked: (SavedComposition) -> Unit,
) {
    val chordsVolumeState = volumeControl.getVoiceVolumeStateFlow(Voice.Chords).collectAsState()
    val metronomeVolumeState = volumeControl.getVoiceVolumeStateFlow(Voice.Metronome).collectAsState()

    val isVolumeDialogOpen = remember { mutableStateOf(false) }
    if (isVolumeDialogOpen.value) {
        Dialog(onDismissRequest = { isVolumeDialogOpen.value = false }) {
            Card {
                VoiceVolumeControls(
                    volumeControl = volumeControl,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }

    Box {
        val isOpen = remember { mutableStateOf(false) }
        IconButton(onClick = { isOpen.value = !isOpen.value }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
        }
        DropdownMenu(
            expanded = isOpen.value,
            onDismissRequest = { isOpen.value = false },
        ) {

            val currentChordCheckState = !chordsVolumeState.value.muted
            DropdownMenuItem(
                onClick = {
                    volumeControl.changeVoiceMuteState(Voice.Chords, currentChordCheckState)
                },
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(
                        checked = currentChordCheckState,
                        onCheckedChange = {
                            volumeControl.changeVoiceMuteState(Voice.Chords, currentChordCheckState)
                        }
                    )
                    Text(text = "Play Chords", maxLines = 1)
                }
            }

            val currentMetronomeCheckState = !metronomeVolumeState.value.muted
            DropdownMenuItem(
                onClick = {
                    volumeControl.changeVoiceMuteState(Voice.Metronome, currentMetronomeCheckState)
                },
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(
                        checked = currentMetronomeCheckState,
                        onCheckedChange = {
                            volumeControl.changeVoiceMuteState(Voice.Metronome, currentMetronomeCheckState)
                        }
                    )
                    Text(text = "Metronome", maxLines = 1)
                }
            }

            DropdownMenuItem(
                onClick = {
                    isOpen.value = false
                    isVolumeDialogOpen.value = true
                },
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Text("Volume", maxLines = 1)
            }

            DropdownMenuItem(
                onClick = {
                    isOpen.value = false
                    if (savedComposition != null) {
                        onExportClicked(savedComposition)
                    }
                },
                enabled = savedComposition != null,
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Text("Export as MIDI", maxLines = 1)
            }
        }
    }
}
