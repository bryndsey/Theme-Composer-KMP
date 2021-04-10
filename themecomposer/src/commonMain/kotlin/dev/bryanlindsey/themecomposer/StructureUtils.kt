package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.*

fun main() {
    println("C Major scale")
    println(constructSpecificScaleIntervals(NamedPitch.C, ScaleType.MAJOR))
    println("A Minor scale")
    println(constructSpecificScaleIntervals(NamedPitch.A, ScaleType.MINOR))

    println("C Major chord")
    println(constructSpecificChordIntervals(Chord(NamedPitch.C, ChordType.MAJOR)))
    println("A Minor chord")
    println(constructSpecificChordIntervals(Chord(NamedPitch.A, ChordType.MINOR)))

    println("G Major chord")
    println(constructSpecificChordNamedNotes(Chord(NamedPitch.G, ChordType.MAJOR)))
    println("A Minor chord")
    println(constructSpecificChordNamedNotes(Chord(NamedPitch.A, ChordType.MINOR)))
    println("C7 chord")
    println(constructSpecificChordNamedNotes(Chord(NamedPitch.C, ChordType.DOMINANT_SEVENTH)))
    println("Em7 chord")
    println(constructSpecificChordNamedNotes(Chord(NamedPitch.E, ChordType.MINOR_SEVENTH)))
}

fun constructSpecificScaleIntervals(scaleRootPitch: NamedPitch, scaleType: ScaleType): List<Int> {
    val rootOffset = scaleRootPitch.semitoneOffsetCountFromC
    return scaleType.intervalsFromScaleRoot.map {
        rootOffset + it.semitoneOffsetFromReferenceNote
    }
}

fun constructSpecificScaleNamedNotes(scale: Scale): List<NamedPitch> {
    val rootOffsetFromC = scale.rootPitch.semitoneOffsetCountFromC
    val scaleOffsets = scale.type.intervalsFromScaleRoot.map {
        rootOffsetFromC + it.semitoneOffsetFromReferenceNote
    }

    return getNamedPitchesFrom(scaleOffsets)
}

fun constructSpecificChordIntervals(chord: Chord): List<Int> {
    val rootOffset = chord.rootPitch.semitoneOffsetCountFromC
    return chord.type.intervalsFromChordRoot.map {
        rootOffset + it.semitoneOffsetFromReferenceNote
    }
}

fun constructSpecificChordNamedNotes(chord: Chord): List<NamedPitch> {
    val intervals = constructSpecificChordIntervals(chord)
    return getNamedPitchesFrom(intervals)
}

fun getNamedPitchesFrom(semitonesFromC: List<Int>): List<NamedPitch> {
    return semitonesFromC.mapNotNull { semitoneOffsetFromC ->
        NamedPitch.values().find {
            it.semitoneOffsetCountFromC == semitoneOffsetFromC % (NamedPitch.values().size)
        }
    }
}

enum class PitchDisplayType {
    ALL_FLAT,
    ALL_SHARP,
    SHARP_SLASH_FLAT,
}

fun NamedPitch.getDisplayName(
    pitchDisplayType: PitchDisplayType = PitchDisplayType.SHARP_SLASH_FLAT
): String {
    val baseString = when (this) {
        NamedPitch.A -> "A"
        NamedPitch.A_SHARP_B_FLAT -> "A♯/B♭"
        NamedPitch.B -> "B"
        NamedPitch.C -> "C"
        NamedPitch.C_SHARP_D_FLAT -> "C♯/D♭"
        NamedPitch.D -> "D"
        NamedPitch.D_SHARP_E_FLAT -> "D♯/E♭"
        NamedPitch.E -> "E"
        NamedPitch.F -> "F"
        NamedPitch.F_SHARP_G_FLAT -> "F♯/G♭"
        NamedPitch.G -> "G"
        NamedPitch.G_SHARP_A_FLAT -> "G♯/A♭"
    }

    return when(pitchDisplayType) {
        PitchDisplayType.ALL_FLAT -> baseString.split("/").last()
        PitchDisplayType.ALL_SHARP -> baseString.split("/").first()
        PitchDisplayType.SHARP_SLASH_FLAT -> baseString
    }
}

fun ScaleType.getDisplayName(): String {
    return toString()
        .split("_")
        .joinToString(" ") {
            it.toLowerCase().capitalize()
        }
}

fun ChordType.getDisplayName(): String {
    return toString()
        .split("_")
        .joinToString(" ") {
            it.toLowerCase().capitalize()
        }
}

fun ChordType.getShortDisplayName(): String {
    return when(this) {
        ChordType.MAJOR -> ""
        ChordType.MINOR -> "m"
        ChordType.DIMINISHED -> "°"
        ChordType.DOMINANT_SEVENTH -> "7"
        ChordType.MAJOR_SEVENTH -> "M7"
        ChordType.MINOR_SEVENTH -> "m7"
        ChordType.AUGMENTED -> "+"
        ChordType.SUSPENDED_SECOND -> "sus2"
        ChordType.SUSPENDED_FOURTH -> "sus4"
    }
}

fun Chord.getDisplayName(
    pitchDisplayType: PitchDisplayType = PitchDisplayType.ALL_SHARP
): String {
    return "${rootPitch.getDisplayName(pitchDisplayType)} ${type.getDisplayName()}"
}

fun Chord.getShortDisplayName(
    pitchDisplayType: PitchDisplayType = PitchDisplayType.ALL_SHARP
): String {
    return "${rootPitch.getDisplayName(pitchDisplayType)}${type.getShortDisplayName()}"
}

fun MidiInstrument.getDisplayName(): String {
    return name.replace("_", " ")
}
