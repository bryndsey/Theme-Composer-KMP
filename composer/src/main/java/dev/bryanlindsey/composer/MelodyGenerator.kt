package dev.bryanlindsey.composer

import dev.bryanlindsey.composer.musicparts.Note
import dev.bryanlindsey.composer.musicparts.NoteTiming
import dev.bryanlindsey.composer.musicparts.ScaleDegree
import kotlin.random.Random

object MelodyGenerator {

    // TODO: Tweak these and make them better
    private const val firstNoteRestProbability = 0.125f
    private const val lastNoteRestProbability = 0.3f
    private const val otherNoteRestProbability = 0.05f

    // This roughly correlates to note "stability" within a chord
    private val basePitchProbabilityMap = NoteTransitionProbability(
        10f,
        1f,
        7f,
        1f,
        7f,
        1f,
        0.25f
    )

    private val noteDistanceTransitionProbabilityMatrix = mapOf(
        ScaleDegree.FIRST to NoteTransitionProbability(0.5f, 8f, 5f, 1f, 1f, 5f, 8f),
        ScaleDegree.SECOND to NoteTransitionProbability(8f, 0.5f, 8f, 5f, 1f, 1f, 5f),
        ScaleDegree.THIRD to NoteTransitionProbability(5f, 8f, 0.5f, 8f, 5f, 1f, 1f),
        ScaleDegree.FOURTH to NoteTransitionProbability(1f, 5f, 8f, 0.5f, 8f, 5f, 1f),
        ScaleDegree.FIFTH to NoteTransitionProbability(1f, 1f, 5f, 8f, 0.5f, 8f, 5f),
        ScaleDegree.SIXTH to NoteTransitionProbability(5f, 1f, 1f, 5f, 8f, 0.5f, 8f),
        ScaleDegree.SEVENTH to NoteTransitionProbability(8f, 5f, 1f, 1f, 5f, 8f, 0.5f)
    )

    fun generateMelody(noteTimingList: List<NoteTiming>): List<Note> {
        var currentPitch = basePitchProbabilityMap.getNextNote()

        return noteTimingList.mapIndexed { index, noteTiming ->
            val restProbabilityValue = Random.nextFloat()
            when {
                // Check if this should be a rest
                index == 0 && restProbabilityValue < firstNoteRestProbability ||
                        index == noteTimingList.lastIndex && restProbabilityValue < lastNoteRestProbability ||
                        restProbabilityValue < otherNoteRestProbability -> {
                    Note.Rest(noteTiming)
                }

                else -> {
                    val notePitch = currentPitch

                    val nextNoteDistanceProbabilities =
                        noteDistanceTransitionProbabilityMatrix[notePitch]
                    val nextNoteProbabilities =
                        basePitchProbabilityMap.combineWith(nextNoteDistanceProbabilities!!)

                    // TODO: Handle nullability better
                    currentPitch = nextNoteProbabilities.getNextNote()

                    Note.RelativePitchNote(notePitch, noteTiming)
                }
            }
        }
    }

    private data class NoteTransitionProbability(
        private val toRootProbability: Float,
        private val toSecondProbability: Float,
        private val toThirdProbability: Float,
        private val toFourthProbability: Float,
        private val toFifthProbability: Float,
        private val toSixthProbability: Float,
        private val toSeventhProbability: Float
    ) {

        private val noteProbabilityMap = ProbabilityMap(
            ScaleDegree.FIRST to toRootProbability,
            ScaleDegree.SECOND to toSecondProbability,
            ScaleDegree.THIRD to toThirdProbability,
            ScaleDegree.FOURTH to toFourthProbability,
            ScaleDegree.FIFTH to toFifthProbability,
            ScaleDegree.SIXTH to toSixthProbability,
            ScaleDegree.SEVENTH to toSeventhProbability
        )

        fun combineWith(other: NoteTransitionProbability) =
            NoteTransitionProbability(
                toRootProbability * other.toRootProbability,
                toSecondProbability * other.toSecondProbability,
                toThirdProbability * other.toThirdProbability,
                toFourthProbability * other.toFourthProbability,
                toFifthProbability * other.toFifthProbability,
                toSixthProbability * other.toSixthProbability,
                toSeventhProbability * other.toSeventhProbability
            )

        fun getNextNote(): ScaleDegree {
            return noteProbabilityMap.getRandomItem()
        }
    }
}
