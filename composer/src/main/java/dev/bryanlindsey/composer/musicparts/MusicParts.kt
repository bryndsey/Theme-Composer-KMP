package dev.bryanlindsey.composer.musicparts

enum class ScaleDegree(val index: Int) {
    FIRST(0),
    SECOND(1),
    THIRD(2),
    FOURTH(3),
    FIFTH(4),
    SIXTH(5),
    SEVENTH(6)
}

enum class TimeSignature(val beatsPerMeasure: Int, val subdivisions: Int) {
    FOUR_FOUR(4, 4),
    THREE_FOUR(3, 4),
    TWO_FOUR(2, 4),
    SIX_EIGHT(6, 8)
}

// TODO: It might be nice to have measures not strictly be one chord
data class Measure(val chordRoot: ScaleDegree, val noteList: List<Note>)

typealias BeatLengths = Float

data class NoteTiming(val startTimeRelativeToMeasureStart: BeatLengths, val length: BeatLengths)

enum class ScaleType(
    val intervalSemitonesList: List<Int>
) {
    MAJOR(listOf(0, 2, 4, 5, 7, 9, 11)),
    MINOR(listOf(0, 2, 3, 5, 7, 8, 10))
}

enum class Pitch(val semitonesAboveC: Int) {
    C(0),
    C_SHARP_D_FLAT(1),
    D(2),
    D_SHARP_E_FLAT(3),
    E(4),
    F(5),
    F_SHARP_G_FLAT(6),
    G(7),
    G_SHARP_A_FLAT(8),
    A(9),
    A_SHARP_B_FLAT(10),
    B(11)
}

data class Key(val pitch: Pitch, val scaleType: ScaleType)
