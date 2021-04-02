package dev.bryanlindsey.musicgenerator3.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bryanlindsey.musicgenerator3.player.Voice
import dev.bryanlindsey.musicgenerator3.player.VolumeControl

@Composable
fun VoiceVolumeControls(
    volumeControl: VolumeControl,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        VoiceVolumeControl(
            title = "Melody Volume",
            volumeControl = volumeControl,
            voice = Voice.Melody,
        )
        VoiceVolumeControl(
            title = "Chord Volume",
            volumeControl = volumeControl,
            voice = Voice.Chords,
        )
        VoiceVolumeControl(
            title = "Metronome Volume",
            volumeControl = volumeControl,
            voice = Voice.Metronome,
        )
    }
}

@Composable
fun VoiceVolumeControl(
    title: String,
    volumeControl: VolumeControl,
    voice: Voice,
) {
    val volumeState = volumeControl.getVoiceVolumeStateFlow(voice).collectAsState().value
    Text(text = title)
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconToggleButton(
            checked = !volumeState.muted,
            onCheckedChange = { volumeControl.changeVoiceMuteState(voice, !it) }
        ) {
            val icon = if (volumeState.muted) {
                Icons.Default.VolumeOff
            } else {
                Icons.Default.VolumeUp
            }
            Icon(imageVector = icon, contentDescription = "Mute")
        }
        Slider(
            value = volumeState.volume,
            onValueChange = {
                volumeControl.changeVoiceVolume(voice, it)
            },
            enabled = volumeState.editable && !volumeState.muted,
        )
    }
}
