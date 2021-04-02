package dev.bryanlindsey.musicgenerator3.ui.saved

import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.SavedComposition
import dev.bryanlindsey.musicgenerator3.ui.common.PlaybackControlsViewModel

@Composable
fun SavedCompositionControls(
    savedComposition: SavedComposition?,
    playbackControlsViewModel: PlaybackControlsViewModel,
    modifier: Modifier = Modifier
) {
    val playbackSettingsState = playbackControlsViewModel.playbackControlsStateFlow.collectAsState()

    FloatingActionButton(
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            if (playbackSettingsState.value.isPlaying || savedComposition == null) {
                playbackControlsViewModel.stopPlaying()
            } else {
                playbackControlsViewModel.playComposition(savedComposition.composition)
            }
        },
        modifier = modifier.then(Modifier.size(72.dp)),
    ) {
        if (playbackSettingsState.value.isPlaying) {
            Icon(Icons.Default.Stop, "Stop")
        } else {
            Icon(Icons.Default.PlayArrow, "Play")
        }
    }
}
