package dev.bryanlindsey.musicgenerator3.player

import dev.bryanlindsey.staccato.CHORD_VOICE_INDEX
import dev.bryanlindsey.staccato.MELODY_VOICE_INDEX
import dev.bryanlindsey.staccato.METRONOME_VOICE_INDEX

enum class Voice(
    val voiceIndex: Int,
    val defaultVolume: Float,
    val defaultMutedState: Boolean,
) {
    Melody(MELODY_VOICE_INDEX, 1f, false),
    Chords(CHORD_VOICE_INDEX, 0.5f, true),
    Metronome(METRONOME_VOICE_INDEX, 0.5f, true)
}
