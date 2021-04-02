package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.*
import kotlin.random.Random

private val scaleToRootChordMap = getAllScaleToRootChordMap()

fun generateMelody(
    timeSignature: TimeSignature? = null,
    scale: Scale? = null,
    chordProgression: List<ChordTiming>? = null,
    tempo: Int? = null,
    melodyInstrument: MidiInstrument? = null,
    chordInstrument: MidiInstrument? = null,
    startBeat: Int = 0,
) : Composition {
    val properties = generateRandomProperties()

    return composeTheme(
        tempo ?: properties.tempo,
        timeSignature ?: properties.timeSignature,
        scale ?: properties.scale,
        chordProgression ?: properties.chordProgression,
        melodyInstrument ?: properties.melodyInstrument,
        chordInstrument ?: properties.chordInstrument,
        startBeat
    )
}

fun generateRandomProperties(): CompositionProperties {
    val timeSignature = if (isRandomCheckSuccessful(0.5f)) {
        TimeSignature.FOUR_FOUR
    } else {
        TimeSignature.THREE_FOUR
    }
//    println("Using Time Signature ${timeSignature.beatsPerMeasure}/${timeSignature.beatDivisor}")

    val (key, startChord) = scaleToRootChordMap.random()

//    println(key)

    val chordProgression = listOf(
        ChordTiming(startChord, timeSignature.beatsPerMeasure * 2),
    )

    val tempo = Random.nextInt(60, 200)
//    println("Tempo is $tempo BPM")

    val disallowedInstruments = setOf(
        MidiInstrument.Reverse_Cymbal,
        MidiInstrument.Guitar_Fret_Noise,
        MidiInstrument.Breath_Noise,
        MidiInstrument.Seashore,
        MidiInstrument.Bird_Tweet,
        MidiInstrument.Telephone_Ring,
        MidiInstrument.Helicopter,
        MidiInstrument.Applause,
        MidiInstrument.Gunshot
    )

    val melodyInstrument = MidiInstrument.values()
        .filterNot { disallowedInstruments.contains(it) }
        .random()
    val chordInstrument = MidiInstrument.values()
        .filterNot { disallowedInstruments.contains(it) }
        .random()

    return CompositionProperties(
        tempo,
        timeSignature,
        key,
        chordProgression,
        melodyInstrument,
        chordInstrument,
    )
}

fun getAllScaleToRootChordMap(): List<Pair<Scale, Chord>> {
    val majorChords = listOf(
        ChordType.MAJOR,
        ChordType.MAJOR_SEVENTH,
        ChordType.DOMINANT_SEVENTH,
        ChordType.SUSPENDED_SECOND,
        ChordType.SUSPENDED_FOURTH,
    )

    val minorChords = listOf(
        ChordType.MINOR,
        ChordType.MINOR_SEVENTH,
        ChordType.SUSPENDED_SECOND,
        ChordType.SUSPENDED_FOURTH,
    )

    val scaleTypeToChordTypeMap = ScaleType.values().map { scaleType ->
        val chords = when (scaleType) {
            ScaleType.MAJOR, ScaleType.MAJOR_PENTATONIC, ScaleType.BLUES_MAJOR -> majorChords
            ScaleType.MINOR, ScaleType.MINOR_PENTATONIC, ScaleType.BLUES_MINOR -> minorChords
        }

        scaleType to chords
    }

    val scaleToRootChordMap = scaleTypeToChordTypeMap.flatMap { (scaleType, chordTypes) ->
        chordTypes.flatMap { chordType ->
            NamedPitch.values().map { pitch ->
                Scale(pitch, scaleType) to Chord(pitch, chordType)
            }
        }
    }

    return scaleToRootChordMap
}

// TODO: Find a better home for this
data class CompositionProperties(
    val tempo: Int,
    val timeSignature: TimeSignature,
    val scale: Scale,
    val chordProgression: List<ChordTiming>,
    val melodyInstrument: MidiInstrument,
    val chordInstrument: MidiInstrument,
)
