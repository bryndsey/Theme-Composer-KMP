package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.NoteTiming
import dev.bryanlindsey.themecomposer.structure.TimeSignature
import kotlin.random.Random

fun main() {
    val rhythm = generateRhythm(TimeSignature.FOUR_FOUR, numberOfBeats = 2, startBeat = 4)
    println(rhythm)
    println(getRhythmWithModifiedStartAndLength(TimeSignature.FOUR_FOUR, rhythm, 2, 16, false))
}

// TODO: Move time signature into overload
fun generateRhythm(
    timeSignature: TimeSignature,
    numberOfBeats: Int = timeSignature.beatsPerMeasure,
    startBeat: Int = 0,
    newNoteStartLikelihood: List<Float> = getEighthNoteStartLikelihood(timeSignature)
): List<NoteTiming> {

    val numberOfSubBeats = numberOfBeats * 2
    val startSubBeat = startBeat * 2

    val lastSubBeat = startSubBeat + numberOfSubBeats
    // Choose subdivisions to start a new note
    val noteStarts = (startSubBeat..lastSubBeat).filter { subBeatIndex ->
        val subbeatProbability = newNoteStartLikelihood[subBeatIndex % newNoteStartLikelihood.size]

        // Always start with a note, and always include the end so we can tell how long the last
        // note is
        subBeatIndex == startSubBeat ||
                subBeatIndex == lastSubBeat ||
                isRandomCheckSuccessful(subbeatProbability)
    }.windowed(size = 2, partialWindows = false) { noteStartPairs ->
        val noteStartSubBeat = noteStartPairs[0]
        val noteLength = noteStartPairs[1] - noteStartPairs[0]
        NoteTiming(noteStartSubBeat, noteLength)
    }
    return noteStarts
}

fun getRhythmWithModifiedStartAndLength(
    timeSignature: TimeSignature,
    noteTimings: List<NoteTiming>,
    newStartBeatEighthNote: Int = noteTimings.getEarliestNoteStart(),
    newLength: Int = noteTimings.getEighthNoteLengthOfAllNotes(),
    shiftExistingNoteStartsToNewStartBeat: Boolean = false
) : List<NoteTiming> {

    val newNoteTimings =
        if (shiftExistingNoteStartsToNewStartBeat) {
            val shiftAmount = newStartBeatEighthNote - noteTimings.getEarliestNoteStart()

            noteTimings.map { note ->
                val shiftedNoteStart = note.noteStartEighthNote + shiftAmount
                note.copy(
                    noteStartEighthNote = shiftedNoteStart
                )
            }.toMutableList()
        } else {
            noteTimings.toMutableList()
        }

    val newLastNoteEndSubBeat = newStartBeatEighthNote + newLength - 1

    // Shrink the note timings array if needed
    newNoteTimings.removeIf { note ->
        val noteStart = note.noteStartEighthNote
        val noteEnd = note.getNoteEndEighthNote()
        // Remove any notes that are not in the range at all
        noteEnd < newStartBeatEighthNote || noteStart > newLastNoteEndSubBeat
    }

    if (newNoteTimings.isEmpty()) {
        return generateRhythm(
            timeSignature,
            newLength / 2,
            newStartBeatEighthNote / 2
        )
    }

    val firstNewNote = newNoteTimings[0]
    if (firstNewNote.noteStartEighthNote < newStartBeatEighthNote) {
//        println("Shrinking start")
        val shrinkAmount = newStartBeatEighthNote - firstNewNote.noteStartEighthNote
        val newNoteLength = firstNewNote.noteLengthInEightNotes - shrinkAmount
        newNoteTimings[0] = firstNewNote.copy(
            noteStartEighthNote = newStartBeatEighthNote,
            noteLengthInEightNotes = newNoteLength
        )
    } else if (newStartBeatEighthNote < firstNewNote.noteStartEighthNote) {
//        println("Expanding start")
        val newStartRhythm = generateRhythm(
            timeSignature,
            (firstNewNote.noteStartEighthNote - newStartBeatEighthNote) / 2,
            newStartBeatEighthNote / 2
        )
        newNoteTimings.addAll(0, newStartRhythm)
    }
    val lastNewNote = newNoteTimings.last()
    if (newLastNoteEndSubBeat < lastNewNote.getNoteEndEighthNote()) {
//        println("Shrinking end")
        newNoteTimings[newNoteTimings.lastIndex] = lastNewNote.copy(
            noteLengthInEightNotes = newLastNoteEndSubBeat - lastNewNote.noteStartEighthNote + 1
        )
    } else if (lastNewNote.getNoteEndEighthNote() < newLastNoteEndSubBeat) {
        val nextNoteStartSubBeat = lastNewNote.getNoteEndEighthNote() + 1
//        println("Expanding end. NextNoteStart: $nextNoteStartSubBeat, New last: $newLastNoteEndSubBeat")

        val newEndRhythm = generateRhythm(
            timeSignature,
            (newLastNoteEndSubBeat - lastNewNote.getNoteEndEighthNote() + 1) / 2,
            nextNoteStartSubBeat / 2
        )
        newNoteTimings.addAll(newEndRhythm)
    }

    return newNoteTimings
}

val fourFourEighthNoteStartLikelihood = listOf(
    PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
    SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
    PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
    SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
)

val fourFourEighthNoteStartLikelihoods = listOf(
    fourFourEighthNoteStartLikelihood,
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ).shuffled(),
)

val threeFourEighthNoteStartLikelihood = listOf(
    PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
    SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
    SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
    BACKBEAT_NOTE_START_PROBABILITY,
)

val threeFourEighthNoteStartLikelihoods = listOf(
    threeFourEighthNoteStartLikelihood,
    threeFourEighthNoteStartLikelihood.shuffled(),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
        BACKBEAT_NOTE_START_PROBABILITY,
    ),
    listOf(
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
        PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY,
    ),
)

fun getEighthNoteStartLikelihood(timeSignature: TimeSignature): List<Float> {
    val baseProbs = when (timeSignature) {
        TimeSignature.THREE_FOUR -> THREE_FOUR_RHYTHM_PROBABILITIES
        TimeSignature.FOUR_FOUR -> FOUR_FOUR_RHYTHM_PROBABILITIES
    }
    val expansionFactor = Random.nextDouble()
    val perlinModifier = perlinSequence(
        stepCount = baseProbs.size,
        stepSize = Random.nextDouble(),
        spreadFactor = expansionFactor,
    )
        .map {
            it.toFloat()
        }.normalizeValue()
    return baseProbs.zip(perlinModifier) { base, modifier ->
        base * modifier
    }
}

fun NoteTiming.getNoteEndEighthNote() = noteStartEighthNote + noteLengthInEightNotes - 1

fun Collection<NoteTiming>.getEarliestNoteStart() =
    minOf {
        it.noteStartEighthNote
    }

fun Collection<NoteTiming>.getEighthNoteLengthOfAllNotes() =
    sumOf {
        it.noteLengthInEightNotes
    }
