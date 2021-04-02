package dev.bryanlindsey.composer

object ChordLikelihoodLimitGenerator {

    fun generateChordLikelihoodLimits(): ChordProgressionGenerator.ChordTransitionProbability {
        val secondLikelihood = listOf(0f, 0.5f, 1f, 2f, 3f).random()
        val thirdLikelihood = listOf(0f, 0.5f, 1f, 2f, 3f).random()
        val fourthLikelihood = listOf(0f, 0.5f, 0.75f, 0.9f, 1f).random()
        val fifthLikelihood = listOf(0f, 0.5f, 0.75f, 0.9f, 1f).random()
        val sixthLikelihood = listOf(0f, 0.5f, 1f, 2f, 3f).random()
        val seventhLikelihood = listOf(0f, 0.5f, 1f, 2f, 3f).random()

        val probs =  ChordProgressionGenerator.ChordTransitionProbability(
            1f,
            secondLikelihood,
            thirdLikelihood,
            fourthLikelihood,
            fifthLikelihood,
            sixthLikelihood,
            seventhLikelihood
        )

        println("Chord likelihoods: $probs")

        return probs
    }
}
