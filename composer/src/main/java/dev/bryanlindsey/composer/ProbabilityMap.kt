package dev.bryanlindsey.composer

import kotlin.random.Random

class ProbabilityMap<K>(vararg pairs: Pair<K, Float>) {
    // * is the "spread" operator
    private val internalMap = mapOf(*pairs)

    fun getRandomItem(): K {
        val probabilitySum = internalMap.values.sum()

        // This will give us a float between 0 and [probabilitySum]
        var randomSelection = Random.nextFloat() * probabilitySum

        internalMap.forEach { entry ->
            randomSelection -= entry.value
            if (randomSelection <= 0) {
                return entry.key
            }
        }

        // If we get here, something is probably wrong
        throw IllegalArgumentException()
    }
}
