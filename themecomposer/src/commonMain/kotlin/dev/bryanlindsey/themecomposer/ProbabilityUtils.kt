package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.*

fun main() {
    val stepWiseProbs = createSpecificNoteStepwiseProbabilitiesFromReferenceNote(
        SpecificPitch(NamedPitch.C, 0).getSemitoneOffsetFromMiddleC().toFloat(), 1.5f
    )
    println(stepWiseProbs)
}

fun getScaleNoteProbabilities(
    scale: Scale,
    diatonicNoteProbabilityFactor: Float = DIATONIC_NOTE_PROBABILITY_FACTOR,
    nonDiatonicNoteProbabilityFactor: Float = NON_DIATONIC_NOTE_PROBABILITY_FACTOR
): Map<NamedPitch, Float> {
    val scaleNamedNotes = constructSpecificScaleNamedNotes(scale)

    val scaleProbabilities = NamedPitch.values().map {
        if (scaleNamedNotes.contains(it)) {
            it to diatonicNoteProbabilityFactor
        } else {
            it to nonDiatonicNoteProbabilityFactor
        }
    }.toMap()

    return scaleProbabilities
}

fun getChordNoteProbabilities(
    chord: Chord,
    chordToneProbabilityFactor: Float = CHORD_TONE_PROBABILITY_FACTOR,
    nonChordToneProbabilityFactor: Float = NON_CHORD_TONE_PROBABILITY_FACTOR
): Map<NamedPitch, Float> {
    val scaleNamedNotes = constructSpecificChordNamedNotes(chord)

    val chordProbabilities = NamedPitch.values().map {
        if (scaleNamedNotes.contains(it)) {
            it to chordToneProbabilityFactor
        } else {
            it to nonChordToneProbabilityFactor
        }
    }.toMap()

    return chordProbabilities
}

fun createSpecificNoteStepwiseProbabilitiesFromReferenceNote(
    referencePitchSemitoneOffsetFromMiddleC: Float,
    spreadFactor: Float = 1f
): SpecificPitchProbabilities {
    val pitchDistances = ALL_SPECIFIC_NOTES.map {
        it to (it.getSemitoneOffsetFromMiddleC() - referencePitchSemitoneOffsetFromMiddleC)
    }

    return pitchDistances.map {
        val normallyDistributedProbability =
            getNormalDistributionValue(input = it.second, standardDeviaton = spreadFactor)

        it.first to normallyDistributedProbability.toFloat()
    }.toMap()
}

typealias SpecificPitchProbabilities = Map<SpecificPitch, Float>

fun Map<NamedPitch, Float>.toSpecificPitchProbabilities(): SpecificPitchProbabilities {
    return ALL_SPECIFIC_NOTES.map {
        val probability = this[it.namedPitch] ?: 0f
        it to probability
    }.toMap()
}

// TODO: Rename this to not include limits in the name
fun getSpecificPitchRangeProbabilitiesWithLimits(
    octaveCenterReference: SpecificPitch = MIDDLE_C,
    octaveSpreadFactor: Float = 2f,
    lowerLimit: SpecificPitch = SpecificPitch(NamedPitch.C, LOWEST_OCTAVE),
    upperLimit: SpecificPitch = SpecificPitch(NamedPitch.B, HIGHEST_OCTAVE),
): SpecificPitchProbabilities {
    require(lowerLimit.getSemitoneOffsetFromMiddleC() < upperLimit.getSemitoneOffsetFromMiddleC())

    val originalRange = createSpecificNoteStepwiseProbabilitiesFromReferenceNote(
        octaveCenterReference.getSemitoneOffsetFromMiddleC().toFloat(),
        octaveSpreadFactor
    )

    return originalRange
        .sort()
        .mapValues { entry ->
            if (entry.key.getSemitoneOffsetFromMiddleC() < lowerLimit.getSemitoneOffsetFromMiddleC()
                || entry.key.getSemitoneOffsetFromMiddleC() > upperLimit.getSemitoneOffsetFromMiddleC()
                    ) {
                0f
            } else {
                entry.value
            }
        }
}

fun SpecificPitchProbabilities.sort(): Map<SpecificPitch, Float> {
    return toSortedMap { pitch1, pitch2 ->
        pitch1.getSemitoneOffsetFromMiddleC()
            .compareTo(pitch2.getSemitoneOffsetFromMiddleC())
    }
}
