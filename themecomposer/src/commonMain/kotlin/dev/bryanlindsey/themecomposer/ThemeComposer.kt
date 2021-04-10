package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.*
import kotlin.random.Random

data class Composition(
    val tempo: Int,
    val timeSignature: TimeSignature,
    val scale: Scale,
    val chordProgression: List<ChordTiming>,
    val melody: List<Note>,
    val melodyInstrument: MidiInstrument,
    val chordInstrument: MidiInstrument,
    val startBeat: Int,
)

fun composeTheme(
    tempo: Int,
    timeSignature: TimeSignature,
    scale: Scale,
    chordProgression: List<ChordTiming>,
    melodyInstrument: MidiInstrument,
    chordInstrument: MidiInstrument,
    startBeat: Int = 0,
): Composition {

    randomizeParameters()

    val rangeProbabilities = getSpecificPitchRangeProbabilitiesWithLimits(
        octaveCenterReference = DEFAULT_RANGE_CENTER,
        octaveSpreadFactor = RANGE_SPREAD_FACTOR,
        lowerLimit = SpecificPitch(NamedPitch.B, 2),
        upperLimit = SpecificPitch(NamedPitch.A, 5),
    )

    val scaleNoteProbs = getScaleNoteProbabilities(scale)
        .toSpecificPitchProbabilities()
        .combineWith(rangeProbabilities)

    val rhythms = mutableListOf<NoteTiming>()
    val melodies = mutableListOf<SpecificPitch>()

    var chordStartBeat = startBeat

    val maxNoteLength = chordProgression.maxOf { it.chordLengthInBeats }
    val mainRhythmLength = Random.nextInt(1, maxNoteLength + 1)
//    println("Main rhythm length: $mainRhythmLength")
    val mainRhythm = generateRhythm(timeSignature, mainRhythmLength)

    // TODO: Rather than doing it strictly by each chord, first generate a rhythm for the whole
    //  length (which could involve generating/repeating a rhythm for each chord), and then step
    //  through each note and match it up with the underlying chord to determine probabilities.
    //  This way, I could have more fine-grained control of probabilities for each note, and could
    //  maybe do things like change the probability of a note depending on previous note values (i.e.
    //  maybe like a generalization of the stepwise melody logic)
    //  I think a big goal here would be to better handle generating main notes. Right now they are
    //  always generated at the beginning and end of a chord, but this gives a fairly limited set of
    //  notes to work with
    chordProgression.onEach { (chord, length) ->
        val chordNoteProbs = getScaleNoteProbabilities(scale)
            .combineWith(getChordNoteProbabilities(chord))
            .toSpecificPitchProbabilities()
            .combineWith(rangeProbabilities)

        val rhythm = if (isRandomCheckSuccessful(RHYTHM_REPEAT_CHANCE)) {
            val chordStartBeatFromBeginningOfMeasure = chordStartBeat % timeSignature.beatsPerMeasure
            val newRhythm = getRhythmWithModifiedStartAndLength(
                timeSignature = timeSignature,
                noteTimings = mainRhythm,
                newStartBeatEighthNote = chordStartBeatFromBeginningOfMeasure * 2,
                newLength = length * 2,
                shiftExistingNoteStartsToNewStartBeat = true
            )

            // Ensure that the note starts are accurate within the overall composition
            newRhythm.map {
                it.copy(
                    noteStartEighthNote = it.noteStartEighthNote +
                            ((chordStartBeat - chordStartBeatFromBeginningOfMeasure) * 2)
                )
            }
        } else {
            generateRhythm(timeSignature, numberOfBeats = length, startBeat = chordStartBeat)
        }

        rhythms.addAll(rhythm)

        val noteLengths = rhythm.map { it.noteLengthInEightNotes }
        val melodyNotes = getMainBasedMelodyFromSpecificPitchProbabilities(
            noteLengths,
            chordNoteProbs,
            scaleNoteProbs
        )
        melodies.addAll(melodyNotes)

        chordStartBeat += length
    }

    val notes = melodies.zip(rhythms) { pitch, timing ->
        Note(pitch, timing)
    }

    return Composition(
        tempo = tempo,
        timeSignature = timeSignature,
        scale = scale,
        chordProgression = chordProgression,
        melody = notes,
        melodyInstrument = melodyInstrument,
        chordInstrument = chordInstrument,
        startBeat = startBeat
    )
}

fun getMainBasedMelodyFromSpecificPitchProbabilities(
    noteLengths: List<Int>,
    mainNoteProbabilities: SpecificPitchProbabilities,
    passingNoteProbabilities: SpecificPitchProbabilities
): List<SpecificPitch> {
    val maxNoteLength = noteLengths.maxOf {
        it
    }

    val mainNoteSequence = noteLengths.mapIndexed { index, noteLength ->
        val mainNoteProbability =
            MAIN_NOTE_LIKELIHOOD_SCALING_FACTOR * (noteLength.toFloat() / maxNoteLength.toFloat())

        // Always have first and last note be a main note. Otherwise, shoot for target percentage of main notes
        if (index == 0 || index == noteLengths.lastIndex || isRandomCheckSuccessful(mainNoteProbability)) {
            mainNoteProbabilities.getRandomItem()
        } else {
            null
        }
    }

//    println(mainNoteSequence)

    // Get the indices of notes that have nulls between them
    val indicesOfPassingNoteWindows = mainNoteSequence.mapIndexed { index, note ->
        if (note == null) null else index
    }.filterNotNull()
        .windowed(size = 2, partialWindows = false)
        .filter { it[1] - it[0] > 1 }

    val updatedSequence = mainNoteSequence.toMutableList()
    indicesOfPassingNoteWindows.onEach {
        val startIndex = it[0]
        val endIndex = it[1]
        val melody = getStepwiseMelody(
            endIndex - startIndex - 1,
            mainNoteSequence[startIndex]!!,
            mainNoteSequence[endIndex]!!,
            passingNoteProbabilities
        )

        melody.onEachIndexed() { index, pitch ->
            updatedSequence[startIndex + index + 1] = pitch
        }
    }

    // No items should be null at this point - this is to make the type system happy
    return updatedSequence.filterNotNull()
}

fun getStepwiseMelody(
    numberOfNotes: Int,
    referenceStartNote: SpecificPitch,
    referenceEndNote: SpecificPitch,
    probabilities: Map<SpecificPitch, Float>,
): List<SpecificPitch> {
    val distanceBetweenReferenceNotes =
        getSemitoneDistanceBetween(referenceStartNote, referenceEndNote)

    return (1..numberOfNotes).map {
        // This will tell us roughly the number of semitones we should have moved from the start tone
        // for this number of note. This will likely be a fractional value, which is fine - we'll
        // end up choosing an integer semitone value in the end, but it will be based around this
        // value.
        val interpolationValue =
            (it.toFloat() / (numberOfNotes.toFloat() + 1f)) * distanceBetweenReferenceNotes.toFloat()

        val stepProbabilities = createSpecificNoteStepwiseProbabilitiesFromReferenceNote(
            referenceStartNote.getSemitoneOffsetFromMiddleC() + interpolationValue,
            STEPWISE_SPREAD_FACTOR
        )

        val finalProbabilities = probabilities.combineWith(stepProbabilities)

        finalProbabilities.getRandomItem()
    }
}
