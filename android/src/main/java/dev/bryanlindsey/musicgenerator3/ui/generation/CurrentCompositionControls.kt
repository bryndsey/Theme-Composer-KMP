package dev.bryanlindsey.musicgenerator3.ui.generation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.common.PlaybackControlsViewModel
import dev.bryanlindsey.themecomposer.Composition

@Composable
fun CurrentCompositionControls(
    composition: Composition?,
    playbackControlsViewModel: PlaybackControlsViewModel,
    onComposeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val controlsState = playbackControlsViewModel.playbackControlsStateFlow.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                if (controlsState.value.isPlaying || composition == null) {
                    playbackControlsViewModel.stopPlaying()
                } else {
                    playbackControlsViewModel.playComposition(composition)
                }
            },
            modifier = Modifier.size(56.dp)
        ) {
            if (controlsState.value.isPlaying) {
                Icon(Icons.Default.Stop, "Stop")
            } else {
                Icon(Icons.Default.PlayArrow, "Play")
            }
        }
        FloatingActionButton(
            backgroundColor = MaterialTheme.colors.primary,
            onClick = {
                onComposeClicked()
                playbackControlsViewModel.stopPlaying()
            },
            modifier = Modifier.size(72.dp)
        ) {
            Icon(Icons.Default.Shuffle, "Compose")
        }
    }
}
