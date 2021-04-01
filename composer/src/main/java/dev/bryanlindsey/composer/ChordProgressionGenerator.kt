package dev.bryanlindsey.composer

import dev.bryanlindsey.composer.musicparts.ScaleDegree
import dev.bryanlindsey.composer.musicparts.ScaleType
import kotlin.random.Random

fun main() {
    repeat(10) {
        val scaleType = ScaleType.MINOR // ScaleType.values().random()
        println("Scale type: $scaleType")
        println(
            ChordProgressionGenerator.generateChords(
                8,
                scaleType,
                ChordLikelihoodLimitGenerator.generateChordLikelihoodLimits()
            )
        )
    }
}

object ChordProgressionGenerator {

    private const val premadeProgressionProbability = 0.25f

    private const val numberOfChordsInPremadeProgressions = 4

    private val premadeProgressions = listOf(
        listOf(ScaleDegree.FIRST, ScaleDegree.FIFTH, ScaleDegree.SIXTH, ScaleDegree.FOURTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.SIXTH, ScaleDegree.FOURTH, ScaleDegree.FIFTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.THIRD, ScaleDegree.FOURTH, ScaleDegree.FIFTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.FIRST, ScaleDegree.FOURTH, ScaleDegree.FIFTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.FOURTH, ScaleDegree.FIFTH, ScaleDegree.FIRST),
        listOf(ScaleDegree.FIRST, ScaleDegree.FOURTH, ScaleDegree.FIRST, ScaleDegree.FIFTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.FIRST, ScaleDegree.FOURTH, ScaleDegree.FOURTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.FIRST, ScaleDegree.FIFTH, ScaleDegree.FIFTH),
        listOf(ScaleDegree.FIRST, ScaleDegree.FIRST, ScaleDegree.FIFTH, ScaleDegree.FIRST),
        listOf(ScaleDegree.FIRST, ScaleDegree.FIFTH, ScaleDegree.FIRST, ScaleDegree.FIRST),
        listOf(ScaleDegree.FOURTH, ScaleDegree.THIRD, ScaleDegree.SECOND, ScaleDegree.FIRST)
    )

    private val firstChordProbabilities = ChordTransitionProbability(10f, 1f, 0.5f, 5f, 5f, 0.5f, 0f)

    private val majorScaleChordProbabilityMatrix = mapOf(
        ScaleDegree.FIRST to ChordTransitionProbability(2f, 1f, 1f, 3f, 3f, 2f, 0f),
        ScaleDegree.SECOND to ChordTransitionProbability(1f, 1f, 0.5f, 1f, 2f, 0.5f, 0f),
        ScaleDegree.THIRD to ChordTransitionProbability(1f, 1f, 0.5f, 2f, 2f, 0.5f, 0f),
        ScaleDegree.FOURTH to ChordTransitionProbability(5f, 1f, 0.5f, 3f, 5f, 1f, 0f),
        ScaleDegree.FIFTH to ChordTransitionProbability(7f, 1f, 0.5f, 4f, 3f, 4f, 0.1f),
        ScaleDegree.SIXTH to ChordTransitionProbability(1f, 1f, 0.5f, 3f, 4f, 0.5f, 0.1f),
        ScaleDegree.SEVENTH to ChordTransitionProbability(5f, 0.1f, 0.1f, 0.5f, 1f, 2f, 0f)
    )

    private val minorScaleChordProbabilityMatrix = mapOf(
        ScaleDegree.FIRST to ChordTransitionProbability(2f, 0.1f, 1f, 3f, 3f, 2f, 0.5f),
        ScaleDegree.SECOND to ChordTransitionProbability(1f, 0f, 0.5f, 1f, 2f, 0.5f, 1f),
        ScaleDegree.THIRD to ChordTransitionProbability(1f, 0.1f, 0.5f, 2f, 2f, 0.5f, 2f),
        ScaleDegree.FOURTH to ChordTransitionProbability(5f, 0f, 0.5f, 3f, 5f, 1f, 3f),
        ScaleDegree.FIFTH to ChordTransitionProbability(7f, 0f, 0.5f, 4f, 3f, 4f, 1f),
        ScaleDegree.SIXTH to ChordTransitionProbability(1f, 0f, 0.5f, 3f, 4f, 0.5f, 4f),
        ScaleDegree.SEVENTH to ChordTransitionProbability(5f, 0.1f, 0.1f, 0.5f, 1f, 2f, 0.25f)
    )

    fun generateChords(
        numberOfChords: Int,
        scaleType: ScaleType,
        chordLikelihoods: ChordTransitionProbability
    ): List<ScaleDegree> {
        val firstChord = firstChordProbabilities.combineWith(chordLikelihoods).getNextChord()
        var currentChord = firstChord

        var chordList = mutableListOf(firstChord)

        val chordProbabilityMatrix = scaleType.getChordTransitionProbabilities()

        repeat(numberOfChords - 1) {
            val nextTransitionProbability =
                chordLikelihoods.combineWith(chordProbabilityMatrix[currentChord]!!)
            // TODO: Handle nullability better
            val nextChord = nextTransitionProbability!!.getNextChord()
            chordList.add(nextChord)
            currentChord = nextChord
        }

        // TODO: This isn't super efficient, as it basically undoes some random generation
        //  Maybe see if there is a more efficient way to do this, especially if performance
        //  starts to become a problem.
        if (numberOfChords >= numberOfChordsInPremadeProgressions && Random.nextFloat() < premadeProgressionProbability) {
            val chosenPremadeProgression = premadeProgressions.random()
            println("Chose progression $chosenPremadeProgression")

            val replaceEndInsteadOfBeginning = Random.nextFloat() < 0.5f
            if (replaceEndInsteadOfBeginning) {
                chordList = chordList.dropLast(numberOfChordsInPremadeProgressions).toMutableList()
                chordList.addAll(chosenPremadeProgression)
            } else {
                chordList = chordList.drop(numberOfChordsInPremadeProgressions).toMutableList()
                chordList.addAll(0, chosenPremadeProgression)
            }
        }

        return chordList
    }

    private fun ScaleType.getChordTransitionProbabilities() =
        when (this) {
            ScaleType.MAJOR -> majorScaleChordProbabilityMatrix
            ScaleType.MINOR -> minorScaleChordProbabilityMatrix
        }

    data class ChordTransitionProbability(
        private val toRootProbability: Float,
        private val toSecondProbability: Float,
        private val toThirdProbability: Float,
        private val toFourthProbability: Float,
        private val toFifthProbability: Float,
        private val toSixthProbability: Float,
        private val toSeventhProbability: Float
    ) {

        private val chordProbabilityMap = ProbabilityMap(
            ScaleDegree.FIRST to toRootProbability,
            ScaleDegree.SECOND to toSecondProbability,
            ScaleDegree.THIRD to toThirdProbability,
            ScaleDegree.FOURTH to toFourthProbability,
            ScaleDegree.FIFTH to toFifthProbability,
            ScaleDegree.SIXTH to toSixthProbability,
            ScaleDegree.SEVENTH to toSeventhProbability
        )

        fun combineWith(other: ChordTransitionProbability) =
            ChordTransitionProbability(
                toRootProbability * other.toRootProbability,
                toSecondProbability * other.toSecondProbability,
                toThirdProbability * other.toThirdProbability,
                toFourthProbability * other.toFourthProbability,
                toFifthProbability * other.toFifthProbability,
                toSixthProbability * other.toSixthProbability,
                toSeventhProbability * other.toSeventhProbability
            )

        fun getNextChord(): ScaleDegree {
            return chordProbabilityMap.getRandomItem()
        }
    }
}
