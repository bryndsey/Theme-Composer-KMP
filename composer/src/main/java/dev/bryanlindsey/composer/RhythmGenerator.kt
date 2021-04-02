package dev.bryanlindsey.composer

import dev.bryanlindsey.composer.musicparts.NoteTiming
import kotlin.random.Random

fun main() {
    println(RhythmGenerator.createRhythm(
        numberOfBeats = 4,
        subdivisionsType = RhythmGenerator.SubdivisionType.TWO_WEIGHTED,
        subdivisionProbabilityFactor = 1f
    ))
}

object RhythmGenerator {

    fun createRhythm(
        numberOfBeats: Int,
        subdivisionsType: SubdivisionType,
        subdivisionProbabilityFactor: Float
    ): List<NoteTiming> {
        // Create one long list of all the probabilities for a beat subdivision to be chosen
        val subdivisionProbabilities = (0 until numberOfBeats)
            .flatMap { subdivisionsType.baseSubdivisionProbability }

        // Choose subdivisions to start a new note
        val noteStarts = subdivisionProbabilities.mapIndexed { index, probability ->
            // Always start with a note, so check index 0
            index == 0 || Random.nextFloat() <= (probability * subdivisionProbabilityFactor)
        }

        val noteStartTimes = noteStarts.mapIndexed { index, isStartOfNote ->
            if (isStartOfNote) {
                val noteStartSinceBeginning =
                    index.toFloat() / subdivisionsType.baseSubdivisionProbability.size
                noteStartSinceBeginning
            } else {
                null
            }
        }.filterNotNull()

        return calculateNoteTimings(noteStartTimes, numberOfBeats)
    }

    private fun calculateNoteTimings(
        noteStartTimings: List<Float>,
        numberOfBeats: Int
    ): List<NoteTiming> {
        val noteLengthsExceptLast =
            noteStartTimings.windowed(size = 2, partialWindows = false) { noteStartPairs ->
                noteStartPairs[1] - noteStartPairs[0]
            }

        val lastNoteLength = numberOfBeats.toFloat() - noteStartTimings.last()

        val noteLengths = noteLengthsExceptLast + lastNoteLength

        return noteStartTimings.zip(noteLengths) { startTime, length ->
            NoteTiming(startTime, length)
        }
    }

    enum class SubdivisionType(val baseSubdivisionProbability: List<Float>) {
        ONE(listOf(0.95f)),
        TWO_WEIGHTED(listOf(0.9f, 0.4f)),
        TWO_EVEN(listOf(0.8f, 0.8f)),
        FOUR_WEIGHTED(listOf(0.9f, 0.025f, 0.4f, 0.025f)),
        FOUR_SEMIWEIGHTED(listOf(0.75f, 0.1f, 0.75f, 0.1f)),
    }

    val subdivisionProbabilityFactors = listOf(1f, 0.75f, 0.5f, 0.25f)
}
