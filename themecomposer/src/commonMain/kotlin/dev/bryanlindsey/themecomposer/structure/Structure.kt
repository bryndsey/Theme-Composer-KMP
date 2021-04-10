package dev.bryanlindsey.themecomposer.structure

import dev.bryanlindsey.themecomposer.getSemitoneOffsetFromMiddleC

val MIDDLE_C = SpecificPitch(NamedPitch.C, 4)
const val LOWEST_OCTAVE: Octave = 0
const val HIGHEST_OCTAVE: Octave = 8

val SEMITONES_PER_OCTAVE = NamedPitch.values().size

val ALL_SPECIFIC_NOTES: List<SpecificPitch> = (LOWEST_OCTAVE..HIGHEST_OCTAVE).flatMap { octave ->
    NamedPitch.values().map { namedPitch ->
        SpecificPitch(namedPitch, octave)
    }.sortedBy {
        it.getSemitoneOffsetFromMiddleC()
    }
}

enum class NamedPitch(
    val semitoneOffsetCountFromC: Int
) {
    A(semitoneOffsetCountFromC = 9),
    A_SHARP_B_FLAT(semitoneOffsetCountFromC = 10),
    B(semitoneOffsetCountFromC = 11),
    C(semitoneOffsetCountFromC = 0),
    C_SHARP_D_FLAT(semitoneOffsetCountFromC = 1),
    D(semitoneOffsetCountFromC = 2),
    D_SHARP_E_FLAT(semitoneOffsetCountFromC = 3),
    E(semitoneOffsetCountFromC = 4),
    F(semitoneOffsetCountFromC = 5),
    F_SHARP_G_FLAT(semitoneOffsetCountFromC = 6),
    G(semitoneOffsetCountFromC = 7),
    G_SHARP_A_FLAT(semitoneOffsetCountFromC = 8),
}

typealias Octave = Int

data class SpecificPitch(
    val namedPitch: NamedPitch,
    val octave: Octave
)

// TODO: Figure out if I should have the Octave here or not
enum class Interval(val semitoneOffsetFromReferenceNote: Int) {
    PERFECT_UNISON(0),
    MINOR_2(1),
    MAJOR_2(2),
    MINOR_3(3),
    MAJOR_3(4),
    PERFECT_4(5),
    AUG_4_DIM_5(6),
    PERFECT_5(7),
    MINOR_6(8),
    MAJOR_6(9),
    MINOR_7(10),
    MAJOR_7(11),
    PERFECT_OCTAVE(12)
}

enum class ScaleType(val intervalsFromScaleRoot: List<Interval>) {
    MAJOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_2,
            Interval.MAJOR_3,
            Interval.PERFECT_4,
            Interval.PERFECT_5,
            Interval.MAJOR_6,
            Interval.MAJOR_7,
            Interval.PERFECT_OCTAVE
        )
    ),
    MINOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_2,
            Interval.MINOR_3,
            Interval.PERFECT_4,
            Interval.PERFECT_5,
            Interval.MINOR_6,
            Interval.MINOR_7,
            Interval.PERFECT_OCTAVE
        )
    ),
    MAJOR_PENTATONIC(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_2,
            Interval.MAJOR_3,
            Interval.PERFECT_5,
            Interval.MAJOR_6,
            Interval.PERFECT_OCTAVE
        )
    ),
    MINOR_PENTATONIC(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MINOR_3,
            Interval.PERFECT_4,
            Interval.PERFECT_5,
            Interval.MINOR_7,
            Interval.PERFECT_OCTAVE
        )
    ),
    BLUES_MAJOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_2,
            Interval.PERFECT_4,
            Interval.PERFECT_5,
            Interval.MAJOR_6,
            Interval.PERFECT_OCTAVE
        )
    ),
    BLUES_MINOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MINOR_3,
            Interval.PERFECT_4,
            Interval.MINOR_6,
            Interval.MINOR_7,
            Interval.PERFECT_OCTAVE
        )
    ),
}

data class Scale(val rootPitch: NamedPitch, val type: ScaleType)

enum class ChordType(val intervalsFromChordRoot: List<Interval>) {
    MAJOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_3,
            Interval.PERFECT_5
        )
    ),
    MINOR(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MINOR_3,
            Interval.PERFECT_5
        )
    ),
    DIMINISHED(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MINOR_3,
            Interval.AUG_4_DIM_5
        )
    ),
    AUGMENTED(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_3,
            Interval.MINOR_6
        )
    ),
    DOMINANT_SEVENTH(
        MAJOR.intervalsFromChordRoot + Interval.MINOR_7
    ),
    MAJOR_SEVENTH(
        MAJOR.intervalsFromChordRoot + Interval.MAJOR_7
    ),
    MINOR_SEVENTH(
        MINOR.intervalsFromChordRoot + Interval.MINOR_7
    ),
    SUSPENDED_SECOND(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.MAJOR_2,
            Interval.PERFECT_5
        )
    ),
    SUSPENDED_FOURTH(
        listOf(
            Interval.PERFECT_UNISON,
            Interval.PERFECT_4,
            Interval.PERFECT_5
        )
    ),
}

data class Chord(val rootPitch: NamedPitch, val type: ChordType)

data class ChordTiming(val chord: Chord, val chordLengthInBeats: Int)

enum class TimeSignature(val beatsPerMeasure: Int, val beatDivisor: Int) {
    THREE_FOUR(3, 4),
    FOUR_FOUR(4, 4)
}

data class NoteTiming(
    val noteStartEighthNote: Int,
    val noteLengthInEightNotes: Int
)

data class Note(
    val pitch: SpecificPitch,
    val noteTiming: NoteTiming
)
