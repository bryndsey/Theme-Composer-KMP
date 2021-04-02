package dev.bryanlindsey.musicgenerator3.ui.common

import dev.bryanlindsey.musicgenerator3.AppViewModel
import dev.bryanlindsey.musicgenerator3.player.CompositionPlayer
import dev.bryanlindsey.musicgenerator3.player.PlayerInitializer
import dev.bryanlindsey.musicgenerator3.player.PlayerState
import dev.bryanlindsey.themecomposer.Composition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackControlsViewModel @Inject constructor(
    // TODO: Make sure player is initialized before using it
    private val playerInitializer: PlayerInitializer,
    private val player: CompositionPlayer,
) : AppViewModel() {

    val playbackControlsStateFlow = MutableStateFlow(PlaybackControlsViewState())

    init {
        viewModelScope.launch {
            player.playerState.collect {
                playbackControlsStateFlow.value = playbackControlsStateFlow.value.copy(
                    isPlaying = it == PlayerState.PLAYING
                )
            }
        }
    }

    fun playComposition(composition: Composition) {
        val currentPlaybackState = playbackControlsStateFlow.value

        viewModelScope.launch {
            // Not sure if this is the best approach
            if (currentPlaybackState.isPlaying) {
                player.stop()
            }

            player.playComposition(
                composition,
                true,
                true,
            )

        }
    }

    fun stopPlaying() {
        viewModelScope.launch {
            player.stop()
        }
    }
}

data class PlaybackControlsViewState(
    val isPlaying: Boolean = false,
)
