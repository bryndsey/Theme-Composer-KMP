package dev.bryanlindsey.musicgenerator3.ui.saved

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.bryanlindsey.musicgenerator3.SavedComposition
import dev.bryanlindsey.musicgenerator3.ui.common.CompositionDisplay
import dev.bryanlindsey.musicgenerator3.ui.common.PlaybackControlsViewModel

@Composable
fun SavedCompositionDisplay(
    composition: SavedComposition?,
    playbackControlsViewModel: PlaybackControlsViewModel,
) {

    if (composition == null) {
        NoSavedCompositionSelectedDisplay()
    } else {
        Scaffold(
            floatingActionButton = {
                SavedCompositionControls(
                    savedComposition = composition,
                    playbackControlsViewModel = playbackControlsViewModel,
                )
            }
        ) { paddingValues ->
            CompositionDisplay(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                composition = composition.composition,
                name = composition.name,
            )
        }
    }
}

@Composable
fun NoSavedCompositionSelectedDisplay() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Select a saved composition to see it displayed here")
    }
}
