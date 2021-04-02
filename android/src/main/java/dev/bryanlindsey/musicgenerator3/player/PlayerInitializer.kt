package dev.bryanlindsey.musicgenerator3.player

import android.content.Context
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.kshoji.javax.sound.midi.MidiSystem
import jp.kshoji.javax.sound.midi.impl.SequencerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jfugue.player.SequencerManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerInitializer @Inject constructor(@ApplicationContext context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val playerInitializationState: MutableStateFlow<PlayerInitializationState> =
        MutableStateFlow(PlayerInitializationState.INITIALIZING)

    init {
        coroutineScope.launch {
            try {
                val sf = SF2Soundbank(context.assets.open("TimGM6mb.sf2"))

                val synth = SoftSynthesizer()
                synth.open()
                synth.loadAllInstruments(sf)

                // This is required in order to initialize the receiver properly in order to play sound as
                // intended.
                synth.receiver

                MidiSystem.addSynthesizer(synth)
                MidiSystem.addMidiDevice(synth)
                SequencerManager.getInstance().sequencer = SequencerImpl()

                playerInitializationState.value = PlayerInitializationState.FINISHED
            } catch (ex: Exception) {
                playerInitializationState.value = PlayerInitializationState.ERROR
            }
        }
    }
}

enum class PlayerInitializationState {
    INITIALIZING,
    FINISHED,
    ERROR
}
