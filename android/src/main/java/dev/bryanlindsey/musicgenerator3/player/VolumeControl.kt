package dev.bryanlindsey.musicgenerator3.player

import dev.bryanlindsey.musicgenerator3.VolumePersister
import jp.kshoji.javax.sound.midi.Synthesizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jfugue.player.SynthesizerManager
import javax.inject.Inject
import javax.inject.Singleton

// This is defined in the MIDI spec
private const val VOLUME_CONTROL_NUMBER = 7

@Singleton
class VolumeControl @Inject constructor(
    playerInitializer: PlayerInitializer,
    private val volumePersister: VolumePersister
) {

    // TODO: Inject this
    private val synth: Synthesizer get() = SynthesizerManager.getInstance().synthesizer

    private var isInitialized = false

    private val voiceVolumeFlowMap = Voice.values().map { voice ->
        voice to MutableStateFlow(
            VoiceVolumeState(
                volume = volumePersister.getStoredVolume(voice),
                muted = volumePersister.getStoredMutedState(voice),
                editable = isInitialized,
            )
        )
    }.toMap()

    private val initializationScope = CoroutineScope(Dispatchers.IO)

    init {
        initializationScope.launch {
            playerInitializer.playerInitializationState.collect {
                isInitialized = it == PlayerInitializationState.FINISHED

                voiceVolumeFlowMap.forEach { entry ->
                    entry.value.value = entry.value.value.copy(editable = isInitialized)
                    changeVoiceVolume(entry.key, entry.value.value.volume)
                    changeVoiceMuteState(entry.key, entry.value.value.muted)
                }
            }
        }
    }

    fun getVoiceVolumeStateFlow(voice: Voice): StateFlow<VoiceVolumeState> {
        return voiceVolumeFlowMap[voice]
            ?: throw IllegalStateException("Voice volume flow is not found")
    }

    fun changeVoiceVolume(voice: Voice, volume: Float /*Should be in the range 0 to 1*/) {
        try {
            val newState = voiceVolumeFlowMap[voice]!!.value.copy(volume = volume)

            updateSynthesizer(voice, newState)

            voiceVolumeFlowMap[voice]!!.value = newState

            volumePersister.updateStoredVolume(voice, volume)
        } catch (e: Exception) {
            println("Tried to change volume before we were ready")
        }
    }

    private fun getVoiceVolume(voice: Voice): Float {
        return if (!isInitialized) {
            volumePersister.getStoredVolume(voice)
        } else {
            // FIXME: I don't really care for this try-catch...
            try {
                synth.channels[voice.voiceIndex].getController(VOLUME_CONTROL_NUMBER) / 128f
            } catch (e: Exception) {
                volumePersister.getStoredVolume(voice)
            }
        }
    }

    fun changeVoiceMuteState(voice: Voice, muted: Boolean) {
        try {
            val newState = voiceVolumeFlowMap[voice]!!.value.copy(muted = muted)

            updateSynthesizer(voice, newState)

            voiceVolumeFlowMap[voice]!!.value = newState

            volumePersister.updateStoredMutedState(voice, muted)
        } catch (e: Exception) {
            println("Tried to change mute state before we were ready")
        }
    }

    private fun getMutedState(voice: Voice) = volumePersister.getStoredMutedState(voice)

    private fun updateSynthesizer(voice: Voice, state: VoiceVolumeState) {
        val targetMuteVolume = if (state.muted) {
            0
        } else {
            (state.volume * 128).toInt()
        }

        synth.channels[voice.voiceIndex].controlChange(
            VOLUME_CONTROL_NUMBER,
            targetMuteVolume
        )
    }
}

data class VoiceVolumeState(
    val volume: Float,
    val muted: Boolean,
    val editable: Boolean
)
