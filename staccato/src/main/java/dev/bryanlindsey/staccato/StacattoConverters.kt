package dev.bryanlindsey.staccato

import dev.bryanlindsey.themecomposer.Composition
import dev.bryanlindsey.themecomposer.structure.*
import org.jfugue.parser.ParserListenerAdapter
import org.jfugue.pattern.Pattern
import org.jfugue.rhythm.Rhythm
import org.jfugue.theory.Note
import org.staccato.StaccatoParser

val BLOCK_METRONOME_KIT = mapOf(
    'O' to "[HI_WOOD_BLOCK]i",
    '`' to "[LO_WOOD_BLOCK]i",
    '.' to "Ri",
)

val HI_HAT_METRONOME_KIT = mapOf(
    'O' to "[OPEN_HI_HAT]i",
    '`' to "[CLOSED_HI_HAT]i",
    '.' to "Ri",
)

val TRIANGLE_METRONOME_KIT = mapOf(
    'O' to "[OPEN_TRIANGLE]i",
    '`' to "[MUTE_TRIANGLE]i",
    '.' to "Ri",
)

const val MELODY_VOICE_INDEX = 0
const val CHORD_VOICE_INDEX = 1
// This is pre-determined by MIDI/JFugue to be th drum voice
const val METRONOME_VOICE_INDEX = 9

internal fun getStacattoChordName(chord: Chord): String {
    val pitchPart = chord.rootPitch.getStacattoNoteName()

    val chordTypePart = getStaccatoChordType(chord.type)

    return "$pitchPart$chordTypePart"
}

internal fun getStaccatoChordType(chordType: ChordType): String {
    return when (chordType) {
        ChordType.MAJOR -> "maj"
        ChordType.MINOR -> "min"
        ChordType.DIMINISHED -> "dim"
        ChordType.DOMINANT_SEVENTH -> "dom7"
        ChordType.MAJOR_SEVENTH -> "maj7"
        ChordType.MINOR_SEVENTH -> "min7"
        ChordType.AUGMENTED -> "aug"
        ChordType.SUSPENDED_SECOND -> "sus2"
        ChordType.SUSPENDED_FOURTH -> "sus4"
    }
}

internal fun NamedPitch.getStacattoNoteName() =
    when (this) {
        NamedPitch.A -> Note("A")
        NamedPitch.A_SHARP_B_FLAT -> Note("A#")
        NamedPitch.B -> Note("B")
        NamedPitch.C -> Note("C")
        NamedPitch.C_SHARP_D_FLAT -> Note("C#")
        NamedPitch.D -> Note("D")
        NamedPitch.D_SHARP_E_FLAT -> Note("D#")
        NamedPitch.E -> Note("E")
        NamedPitch.F -> Note("F")
        NamedPitch.F_SHARP_G_FLAT -> Note("F#")
        NamedPitch.G -> Note("G")
        NamedPitch.G_SHARP_A_FLAT -> Note("G#")
    }

internal fun SpecificPitch.getStacattoNoteName(): String {
    // The octave needs a +1 to account for different octave counting systems
    return "${this.namedPitch.getStacattoNoteName()}${this.octave + 1}"
}

fun convertNotesToStaccato(
    notes: List<dev.bryanlindsey.themecomposer.structure.Note>,
): String {
    val stacattoString = notes.map { note ->
        val midiNoteValue = note.pitch.getStacattoNoteName()

        // TODO: I'm not entirely sure the divisor here is right - it works for */4 time signatures,
        //  but I'm not sure if it works generically. Think about this more.
        val noteLength = note.noteTiming.noteLengthInEightNotes.toFloat() / 8f

        "$midiNoteValue/$noteLength"
    }
        .joinToString(separator = " ")

//    println("Stacatto string: $stacattoString")

    return stacattoString
}

fun convertChordsToStaccato(
    timeSignature: TimeSignature,
    chordProgression: List<ChordTiming>,
): String {
    val chordString = chordProgression.joinToString(separator = " ") { (chord, length) ->
        val chordName = getStacattoChordName(chord)
        val chordLength = length.toFloat() / timeSignature.beatDivisor.toFloat()
        "$chordName/$chordLength"
    }

//    println(chordString)

    return chordString
}

internal fun getMetronomeStaccato(
    timeSignature: TimeSignature,
    numberOfBeats: Int = timeSignature.beatsPerMeasure,
    startBeat: Int = 0
): String {
    val firstBeatRhythm = "O."
    val otherBeatRhythm = "`."

    val metronomeStaccato = (startBeat until numberOfBeats + startBeat).map {
        if (it % timeSignature.beatsPerMeasure == 0) {
            firstBeatRhythm
        } else {
            otherBeatRhythm
        }
    }.joinToString(separator = "")

//    println(metronomeStaccato)

    return metronomeStaccato
}

fun Composition.convertToPattern(
    includeChords: Boolean = false,
    includeMetronome: Boolean = false,
): Pattern {

    val pattern =
        Pattern(getMelodyStaccato())
            .setTempo(tempo)
            .setVoice(MELODY_VOICE_INDEX)
            .setInstrument(melodyInstrument.name)

    if (includeChords) {
        val chordPattern =
            Pattern(getChordStaccato())
                .setVoice(CHORD_VOICE_INDEX)
                .setInstrument(chordInstrument.name)

        pattern.add(chordPattern)
    }

    if (includeMetronome) {
        val drumRhythm = Rhythm(getMetronomeStaccato())
            .setRhythmKit(BLOCK_METRONOME_KIT)

        pattern.add(drumRhythm)
    }
    return pattern
}

fun Composition.getMelodyStaccato() = convertNotesToStaccato(melody)

fun Composition.getChordStaccato() = convertChordsToStaccato(timeSignature, chordProgression)

fun Composition.getMetronomeStaccato(): String {
    val numberOfBeats = chordProgression.sumOf { it.chordLengthInBeats }
    val startNote = startBeat
    return getMetronomeStaccato(
        timeSignature,
        numberOfBeats,
        startNote
    )
}

fun staccatoMelodyToNotes(staccatoMelody: String): List<dev.bryanlindsey.themecomposer.structure.Note> {
    val parserListener = MelodyParserListener()
    val parser = StaccatoParser()
    parser.addParserListener(parserListener)

    parser.parse(staccatoMelody)
    return parserListener.getNotes()
}

class MelodyParserListener: ParserListenerAdapter() {
    private var currentNoteStartSubbeat = 0
    private val noteList = mutableListOf<dev.bryanlindsey.themecomposer.structure.Note>()

    override fun onNoteParsed(note: Note?) {
        note?.let { jFugueNote ->
            // This is assuming 8th notes are the base subbeat value
            val duration = (jFugueNote.duration * 8).toInt()
            val timing = NoteTiming(currentNoteStartSubbeat, duration)
            currentNoteStartSubbeat += duration

            val noteOctave = jFugueNote.octave - 1
            val pitch = namedPitchFromJFugueNote(jFugueNote) ?: return
            val specificPitch = SpecificPitch(pitch, noteOctave)

            val domainNote = Note(specificPitch, timing)
            noteList.add(domainNote)
        }
    }

    fun getNotes() = noteList as List<dev.bryanlindsey.themecomposer.structure.Note>
}

fun staccatoChordsToChordProgression(staccatoChords: String): List<ChordTiming> {
    val chordListener = ChordProgressionParserListener()
    val chordParser = StaccatoParser()
    chordParser.addParserListener(chordListener)
    chordParser.parse(staccatoChords)

    return chordListener.getChordProgression()
}

class ChordProgressionParserListener: ParserListenerAdapter() {
    private val chordProgression = mutableListOf<ChordTiming>()

    override fun onChordParsed(chord: org.jfugue.theory.Chord?) {
//        println(chord?.toHumanReadableString())
        chord?.let { jFugueChord ->
            val rootNote  = jFugueChord.root

            val pitch = namedPitchFromJFugueNote(rootNote) ?: return

            // Based on quarter note
            val duration = (rootNote.duration * 4).toInt()

            // TODO: There's probably better ways to do this
            val type = jFugueChord.chordType
            val chordType = ChordType.values().find { getStaccatoChordType(it).equals(type, ignoreCase = true) } ?: return

            chordProgression.add(
                ChordTiming(Chord(pitch, chordType), duration)
            )
        }
    }

    fun getChordProgression() = chordProgression as List<ChordTiming>
}

fun namedPitchFromJFugueNote(jFugueNote: Note): NamedPitch? {
    val pitchOffsetFromC = jFugueNote.positionInOctave.toInt()
    return NamedPitch.values().find { it.semitoneOffsetCountFromC == pitchOffsetFromC }
}

fun main() {
    val testString = "C5/0.25 D5/0.25 E5/0.5"

    val parserListener = MelodyParserListener()
    val parser = StaccatoParser()
    parser.addParserListener(parserListener)

    parser.parse(testString)
//    println(parserListener.getNotes())

    val testChordStrinng = "Cmaj7/0.25 Amin/0.25 Gmaj/0.5"

    val chordListener = ChordProgressionParserListener()
    val chordParser = StaccatoParser()
    chordParser.addParserListener(chordListener)
    chordParser.parse(testChordStrinng)

//    println(chordListener.getChordProgression())
}
