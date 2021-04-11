package dev.bryanlindsey.musicgenerator3.player

import dev.bryanlindsey.themecomposer.Composition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jfugue.player.SynthesizerManager

interface ICompositionPlayer {
    suspend fun playComposition(
        composition: Composition,
        playChords: Boolean = false,
        playMetronome: Boolean = false
    )

    suspend fun stop()
}