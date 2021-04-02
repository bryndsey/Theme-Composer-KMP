package dev.bryanlindsey.musicgenerator3.ui.generation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.bryanlindsey.musicgenerator3.ui.common.CompositionDisplay
import dev.bryanlindsey.musicgenerator3.ui.common.PlaybackControlsViewModel
import dev.bryanlindsey.themecomposer.Composition

@Composable
fun CurrentCompositionDisplay(
    composition: Composition?,
    playbackControlViewModel: PlaybackControlsViewModel,
    onComposeClicked: () -> Unit,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            CurrentCompositionControls(
                composition = composition,
                playbackControlsViewModel = playbackControlViewModel,
                onComposeClicked = onComposeClicked,
            )
        }
    ) { paddingValues ->
        CompositionDisplay(
            composition = composition,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}
