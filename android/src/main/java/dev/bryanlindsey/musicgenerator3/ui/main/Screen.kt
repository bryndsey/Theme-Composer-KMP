package dev.bryanlindsey.musicgenerator3.ui.main

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.MidiExporter
import dev.bryanlindsey.musicgenerator3.player.VolumeControl
import dev.bryanlindsey.musicgenerator3.ui.BackdropNavigator
import dev.bryanlindsey.musicgenerator3.ui.common.PlaybackControlsViewModel
import dev.bryanlindsey.musicgenerator3.ui.generation.*
import dev.bryanlindsey.musicgenerator3.ui.saved.SavedCompositionDisplay
import dev.bryanlindsey.musicgenerator3.ui.saved.SavedCompositionMoreOptions
import dev.bryanlindsey.musicgenerator3.ui.saved.SavedCompositionViewModel
import dev.bryanlindsey.musicgenerator3.ui.saved.SavedCompositionsList
import dev.bryanlindsey.themecomposer.Composition
import javax.inject.Inject

sealed class Screen(
    val route: String,
    val label: String,
) {
    object Generation : Screen(
        "generation",
        "Compose",
    )

    object SavedCompositions : Screen(
        "savedcompositions",
        "Saved",
    )

    object HomeNavigation : Screen(
        "home",
        "SongSpark"
    )
}

// This feels kind of weird - I wanted it to be inside of the one class that uses it,
// but I guess this will do for now
@Composable
fun GenerationViewModel.getCurrentComposition(): Composition? {
    val flowState = generationViewStateFlow.collectAsState()
    val viewState = flowState.value
    return if (viewState is GenerationViewState.Content) {
        viewState.composition
    } else {
        null
    }
}

@OptIn(ExperimentalMaterialApi::class)
sealed class BackdropScreenSet(
    val frontScreen: @Composable () -> Unit,
    val backScreen:  @Composable () -> Unit,
    val defaultFocusedScreen: BackdropFocusedScreen,
    val frontTopBarOptions: @Composable RowScope.() -> Unit = {},
    val backTopBarOptions: @Composable RowScope.() -> Unit = {},
) {

    class GenerationSet @Inject constructor(
        private val backdropNavigator: BackdropNavigator,
        private val viewModel: GenerationViewModel,
        private val playbackControlsViewModel: PlaybackControlsViewModel,
        private val volumeControl: VolumeControl,
        private val midiExporter: MidiExporter,
    ) : BackdropScreenSet(
        frontScreen = {
            val currentComposition = viewModel.getCurrentComposition()

            CurrentCompositionDisplay(
                composition = currentComposition,
                playbackControlViewModel = playbackControlsViewModel,
                onComposeClicked = { viewModel.compose() }
            )

            handleBackPress(action = {
                backdropNavigator.navigate(Screen.HomeNavigation)
            })
        },
        backScreen = {
            CompositionParameterControls(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        },
        defaultFocusedScreen = BackdropFocusedScreen.FRONT,
        frontTopBarOptions = {
            IconButton(onClick = { backdropNavigator.setBackdropValue(BackdropValue.Revealed) }) {
                Icon(Icons.Default.Tune, contentDescription = "Composition Settings")
            }

            val composition = viewModel.getCurrentComposition()

            CurrentCompositionMoreOptions(
                composition = composition,
                volumeControl = volumeControl,
                onExportClicked = { midiExporter.exportComposition(it) },
                onSaveClicked = { composition, name -> viewModel.saveComposition(name) }
            )
        },
        backTopBarOptions = {
            IconButton(onClick = { backdropNavigator.setBackdropValue(BackdropValue.Concealed) }) {
                Icon(Icons.Default.Piano, "Piano Roll")
            }
        }
    )

    class SavedSet @Inject constructor(
        private val backdropNavigator: BackdropNavigator,
        private val viewModel: SavedCompositionViewModel,
        private val playbackControlsViewModel: PlaybackControlsViewModel,
        private val volumeControl: VolumeControl,
        private val midiExporter: MidiExporter,
    ) : BackdropScreenSet(
        frontScreen = {
            val compositionState = viewModel.selectedComposition.collectAsState()

            SavedCompositionDisplay(
                composition = compositionState.value,
                playbackControlsViewModel = playbackControlsViewModel,
            )

            handleBackPress(action = {
                backdropNavigator.navigate(Screen.HomeNavigation)
            })
        },
        backScreen = {
            val savedItemsList = viewModel.compositionsState.collectAsState().value

            SavedCompositionsList(
                savedItemsList = savedItemsList,
                onExportClicked = { midiExporter.exportComposition(it) },
                onItemClicked = {
                    viewModel.selectComposition(it)
                    backdropNavigator.setBackdropValue(BackdropValue.Concealed)
                },
                onPlayClicked = {
                    playbackControlsViewModel.playComposition(it.composition)
                },
                onDeleteItemClicked = { viewModel.deleteComposition(it) },
                onItemRenamed = { viewModel.updateComposition(it) }
            )
        },
        defaultFocusedScreen = BackdropFocusedScreen.BACK,
        frontTopBarOptions = {
            IconButton(onClick = { backdropNavigator.setBackdropValue(BackdropValue.Revealed) }) {
                Icon(Icons.Default.QueueMusic, "Saved list")
            }
            val compositionState = viewModel.selectedComposition.collectAsState()

            SavedCompositionMoreOptions(
                savedComposition = compositionState.value,
                volumeControl = volumeControl,
                onExportClicked = { midiExporter.exportComposition(it) }
            )
        }
    )

    class HomeNavigation @Inject constructor(
        private val backdropNavigator: BackdropNavigator,
        private val volumeControl: VolumeControl,
    ) : BackdropScreenSet(
        frontScreen = {
            AppSettingsControls(
                volumeControl = volumeControl,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        },
        backScreen = {
            HomeNavigationScreen(
                onNavigateToGeneration = { backdropNavigator.navigate(Screen.Generation) },
                onNavigateToSave = { backdropNavigator.navigate(Screen.SavedCompositions) }
            )
        },
        defaultFocusedScreen = BackdropFocusedScreen.BACK,
        frontTopBarOptions = {
            IconButton(onClick = { backdropNavigator.setBackdropValue(BackdropValue.Revealed) }) {
                Icon(Icons.Default.Close, "Close")
            }
        }
    )
}

@Composable
fun handleBackPress(action: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action()
        }
    }

    val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current

    DisposableEffect(
        key1 = lifecycleOwner,
        key2 = backPressedDispatcherOwner
    ) {
        backPressedDispatcherOwner
            .onBackPressedDispatcher
            .addCallback(
                lifecycleOwner,
                callback
            )

        onDispose {
            callback.remove()
        }
    }
}

enum class BackdropFocusedScreen {
    FRONT,
    BACK
}
