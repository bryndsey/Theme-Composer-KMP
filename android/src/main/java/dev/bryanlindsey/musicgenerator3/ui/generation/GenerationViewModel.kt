package dev.bryanlindsey.musicgenerator3.ui.generation

import dev.bryanlindsey.musicgenerator3.AppViewModel
import dev.bryanlindsey.musicgenerator3.CompositionRepository
import dev.bryanlindsey.musicgenerator3.player.PlayerInitializationState
import dev.bryanlindsey.musicgenerator3.player.PlayerInitializer
import dev.bryanlindsey.themecomposer.Composition
import dev.bryanlindsey.themecomposer.MidiInstrument
import dev.bryanlindsey.themecomposer.generateMelody
import dev.bryanlindsey.themecomposer.generateRandomProperties
import dev.bryanlindsey.themecomposer.structure.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerationViewModel @Inject constructor(
    private val playerInitializer: PlayerInitializer,
    private val compositionRepository: CompositionRepository
) : AppViewModel() {

    val generationViewStateFlow: MutableStateFlow<GenerationViewState> = MutableStateFlow(GenerationViewState.Loading)

    val parameterState: MutableStateFlow<ParameterState> = MutableStateFlow(ParameterState())

    private var currentComposition: Composition? = null

    init {
        viewModelScope.launch {
            playerInitializer.playerInitializationState.collect {
                when (it) {
                    PlayerInitializationState.INITIALIZING -> {
                    }
                    PlayerInitializationState.FINISHED -> {
                        compose()
                    }
                    PlayerInitializationState.ERROR -> {
                        generationViewStateFlow.value = GenerationViewState.Error
                    }
                }
            }
        }
    }

    fun setIsTimeSignatureRandom(isRandomized: Boolean) {
        parameterState.value = parameterState.value.copy(isTimeSignatureRandom = isRandomized)
    }

    fun setIsTempoRandom(it: Boolean) {
        parameterState.value = parameterState.value.copy(isTempoRandom = it)
    }

    fun setIsScaleRandom(it: Boolean) {
        parameterState.value = parameterState.value.copy(isScaleRandom = it)
    }

    fun setIsChordRandom(it: Boolean) {
        parameterState.value = parameterState.value.copy(isChordRandom = it)
    }

    fun setTimeSignature(timeSignature: TimeSignature) {
        parameterState.value = parameterState.value.copy(timeSignature = timeSignature)
    }

    fun setTempo(tempo: Int) {
        parameterState.value = parameterState.value.copy(tempo = tempo)
    }

    fun setScalePitch(pitch: NamedPitch) {
        val currentState = parameterState.value
        val newScale = currentState.key.copy(rootPitch = pitch)
        parameterState.value = currentState.copy(key = newScale)
    }

    fun setScaleType(scaleType: ScaleType) {
        val currentState = parameterState.value
        val newScale = currentState.key.copy(type = scaleType)
        parameterState.value = currentState.copy(key = newScale)
    }

    fun setChordProgression(chordProgression: List<ChordTiming>) {
        parameterState.value = parameterState.value.copy(chordProgression = chordProgression)
    }

    fun setIsMelodyInstrumentRandom(isRandomized: Boolean) {
        parameterState.value = parameterState.value.copy(isMelodyInstrumentRandom = isRandomized)
    }

    fun setMelodyInstrument(instrument: MidiInstrument) {
        parameterState.value = parameterState.value.copy(melodyInstrument = instrument)
    }

    fun setIsChordInstrumentRandom(isRandomized: Boolean) {
        parameterState.value = parameterState.value.copy(isChordInstrumentRandom = isRandomized)
    }

    fun setChordInstrument(instrument: MidiInstrument) {
        parameterState.value = parameterState.value.copy(chordInstrument = instrument)
    }

    fun compose() {
        generationViewStateFlow.value = GenerationViewState.Loading
        viewModelScope.launch {

            val composition = withContext(Dispatchers.Default) {
                val parameters = parameterState.value

                parameters.run {
                    val randomProperties = generateRandomProperties()

                    val timeSignature =
                        if (!isTimeSignatureRandom) timeSignature else randomProperties.timeSignature
                    val chordProgression = if (isChordRandom) {
                        randomProperties.chordProgression
                    } else {
                        parameters.chordProgression
                    }
                    generateMelody(
                        timeSignature = timeSignature,
                        scale = if (!isScaleRandom) key else randomProperties.scale,
                        chordProgression = chordProgression,
                        tempo = if (!isTempoRandom) tempo else randomProperties.tempo,
                        melodyInstrument = if (isMelodyInstrumentRandom) randomProperties.melodyInstrument else melodyInstrument,
                        chordInstrument = if (isChordInstrumentRandom) randomProperties.chordInstrument else chordInstrument,
                    )
                }

            }
            currentComposition = composition
            setContentState()
        }
    }

    private fun setContentState() {
        val composition = currentComposition!!
        generationViewStateFlow.value = GenerationViewState.Content(composition)
    }

    fun saveComposition(
        name: String?
    ) {
        viewModelScope.launch {
            compositionRepository.saveComposition(
                currentComposition!!,
                name
            )
        }
    }
}

sealed class GenerationViewState {
    object Error : GenerationViewState()
    object Loading : GenerationViewState()
    data class Content(
        val composition: Composition
    ) : GenerationViewState()
}

data class ParameterState(
    val isTimeSignatureRandom: Boolean = true,
    val timeSignature: TimeSignature = TimeSignature.FOUR_FOUR,
    val isTempoRandom: Boolean = true,
    val tempo: Int = 120,
    val isScaleRandom: Boolean = true,
    val key: Scale = Scale(NamedPitch.C, ScaleType.MAJOR),
    val isChordRandom: Boolean = true,
    val chordProgression: List<ChordTiming> = listOf(
        ChordTiming(
            Chord(
                NamedPitch.C,
                ChordType.MAJOR
            ),
            timeSignature.beatsPerMeasure * 2
        )
    ),
    val isMelodyInstrumentRandom: Boolean = true,
    val melodyInstrument: MidiInstrument = MidiInstrument.Steel_String_Guitar,
    val isChordInstrumentRandom: Boolean = true,
    val chordInstrument: MidiInstrument = MidiInstrument.Steel_String_Guitar,
)
