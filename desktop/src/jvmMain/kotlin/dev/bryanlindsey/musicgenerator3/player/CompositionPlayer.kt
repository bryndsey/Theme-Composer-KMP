package dev.bryanlindsey.musicgenerator3.player

import dev.bryanlindsey.staccato.convertToPattern
import dev.bryanlindsey.themecomposer.Composition
import javax.sound.midi.Sequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.jfugue.pattern.Pattern
import org.jfugue.player.ManagedPlayerListener
import org.jfugue.player.Player
import org.jfugue.player.SynthesizerManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositionPlayer @Inject constructor() : ICompositionPlayer {

    val playerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.IDLE)

    private var player = getNewPlayer()

    private fun getNewPlayer(): Player = Player().also { setupPlayerListeners(it) }

    private val playerListener = object : ManagedPlayerListener {
        override fun onStarted(p0: Sequence?) {
            playerState.value = PlayerState.PLAYING
        }

        override fun onFinished() {
            playerState.value = PlayerState.IDLE
        }

        override fun onPaused() {
            playerState.value = PlayerState.PAUSED
        }

        override fun onResumed() {
            playerState.value = PlayerState.PLAYING
        }

        override fun onSeek(p0: Long) {}

        override fun onReset() {
            playerState.value = PlayerState.IDLE
        }
    }

    private fun setupPlayerListeners(player: Player) {
        player.managedPlayer.addManagedPlayerListener(playerListener)
    }

    override suspend fun playComposition(
        composition: Composition,
        playChords: Boolean,
        playMetronome: Boolean
    ) {
        playPattern(
            composition.convertToPattern(
                includeChords = playChords,
                includeMetronome = playMetronome,
            )
        )
    }

    private suspend fun playPattern(pattern: Pattern) {
        if (playerState.value == PlayerState.PLAYING) {
            stop()
        }

        withContext(Dispatchers.Default) {
            player = getNewPlayer()

            player.play(pattern)
        }
    }

    override suspend fun stop() {
        withContext(Dispatchers.Default) {
            if (playerState.value == PlayerState.PLAYING) {
                SynthesizerManager.getInstance().synthesizer.channels.onEach {
                    it.allSoundOff()
                }

                player.managedPlayer.reset()
            }
        }
    }
}

enum class PlayerState {
    IDLE,
    PLAYING,
    PAUSED,
}
