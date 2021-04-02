package dev.bryanlindsey.musicgenerator3.ui.generation

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
import dev.bryanlindsey.musicgenerator3.player.Voice
import dev.bryanlindsey.musicgenerator3.player.VolumeControl
import dev.bryanlindsey.musicgenerator3.ui.common.NameCompositionDialog
import dev.bryanlindsey.musicgenerator3.ui.main.VoiceVolumeControls
import dev.bryanlindsey.themecomposer.Composition

@Composable
fun CurrentCompositionMoreOptions(
    composition: Composition?,
    volumeControl: VolumeControl,
    onExportClicked: (Composition) -> Unit,
    onSaveClicked: (Composition, String) -> Unit,
) {

    val compositionControlsEnabled = composition != null

    val chordsVolumeState = volumeControl.getVoiceVolumeStateFlow(Voice.Chords).collectAsState()
    val metronomeVolumeState = volumeControl.getVoiceVolumeStateFlow(Voice.Metronome).collectAsState()


    val isNameDialogOpen = remember { mutableStateOf(false) }
    if (isNameDialogOpen.value) {
        NameCompositionDialog(
            onNameConfirmed = {
                // FIXME: This null check is silly/annoying
                if (composition != null) {
                    onSaveClicked(composition, it)
                }
                isNameDialogOpen.value = false
            },
            onDismiss = { isNameDialogOpen.value = false },
            title = "Save"
        )
    }

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
                    if (composition != null) {
                        onExportClicked(composition)
                    }
                },
                enabled = compositionControlsEnabled,
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Text("Export as MIDI", maxLines = 1)
            }
            DropdownMenuItem(
                onClick = {
                    isOpen.value = false
                    isNameDialogOpen.value = true
                },
                enabled = compositionControlsEnabled,
                modifier = Modifier.wrapContentWidth(unbounded = true),
            ) {
                Text("Save", maxLines = 1)
            }
        }
    }
}
