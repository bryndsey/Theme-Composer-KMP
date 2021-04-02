package dev.bryanlindsey.musicgenerator3.ui.main

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.player.VolumeControl
import dev.bryanlindsey.musicgenerator3.ui.common.ComingSoonContent

@Composable
fun AppSettingsControls(
    volumeControl: VolumeControl,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = spacedBy(8.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        VoiceVolumeControls(
            volumeControl = volumeControl,
        )
        ComingSoonContent()
    }
}

