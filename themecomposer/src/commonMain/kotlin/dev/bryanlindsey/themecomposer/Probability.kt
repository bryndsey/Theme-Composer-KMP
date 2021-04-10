package dev.bryanlindsey.themecomposer

import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

fun Random.nextFloat(until: Float): Float {
    return nextFloat(0f, until)
}

fun Random.nextFloat(from: Float, until: Float): Float {
    val rangeSize = until - from
    return nextFloat() * rangeSize + from
}

fun isRandomCheckSuccessful(percentageChanceFromZeroToOne: Float): Boolean {
    return percentageChanceFromZeroToOne > Random.nextFloat()
}

fun <K> Map<K, Float>.getRandomItem(): K {
    val probabilitySum = values.sum()

    // This will give us a float between 0 and [probabilitySum]
    var randomSelection = Random.nextFloat() * probabilitySum

    forEach { entry ->
        randomSelection -= entry.value
        if (randomSelection <= 0) {
            return entry.key
        }
    }

    // If we get here, something is probably wrong
    throw IllegalArgumentException()
}

fun <K> Map<K, Float>.combineWith(otherMap: Map<K, Float>): Map<K, Float> {
    return combineProbabilities(this, otherMap)
}

fun <K> combineProbabilities(vararg maps: ProbabilityMap<K>)
        : ProbabilityMap<K>{
    val equalWeightedMaps = maps.map {
        WeightedProbabilityMap(it, 1f)
    }

    return combineProbabilitiesWithWeights(*equalWeightedMaps.toTypedArray())
}

fun <K> combineProbabilitiesWithWeights(
    vararg weightedMaps: WeightedProbabilityMap<K>
): ProbabilityMap<K> {
    // Use this to scale all probabilities such that the max one ends up as 1
    // This is needed for a few reasons:
    // First, if every other weight is 0 other than the max, then the probability that is returned
    // would be a scaled probability rather than the original max as intended
    // Secondly, it also also means that every weight will be 0 <= weight <= 1 (assuming no negative
    // weights are passed in. Since we are exponentiating (is that a real word?) each probability
    // by the weight, it keeps the values from overflowing. If things get rounded down to 0 due to
    // float arithmetic inaccuracies, it would be because the values are so small anyway that they
    // might as well be zero. Honestly, I'm not like super concerned about 100% accuracy with the
    // weights - just something to get the general idea.
    val maxWeight = weightedMaps.maxOf { it.weight }

    val commonKeys = weightedMaps
        .map { it.map.keys }
        .reduce { set1, set2 ->
            set1.intersect(set2)
        }

    return commonKeys.map { key ->
        val combinedWeight = weightedMaps.map { map ->
            (map.map[key] ?: 0f).pow(map.weight / maxWeight)
        }
            .reduce { left, right ->
                left * right
            }
        key to combinedWeight
    }.toMap()
}

fun linearInterpolate(value1: Float, value2: Float, interpolationFactor: Float): Float {
    // TODO: Figure out which of these algorithms is better to use
//    return (1-interpolationFactor)*value1 + interpolationFactor*value2
    return value1 + ((value2 - value1) * interpolationFactor)
}

fun List<Float>.normalizeValue(): List<Float> {
    val maxValue = this.filter { it != 0f }.max() ?: throw IllegalArgumentException("You must have some non-zero probabilities")

    return map {
        it / maxValue
    }
}

fun <K> Map<K, Float>.normalizeProbabilities(): Map<K, Float> {
    // TODO: Figure out a better way to handle this in case the who prob list is 0s
    val maxValue = this.values.filter { it != 0f }.max() ?: throw IllegalArgumentException("You must have some non-zero probabilities")

    return this.mapValues {
        it.value / maxValue
    }
}

fun getNormalDistributionValue(
    input: Float,
    mean: Float = 0f,
    standardDeviaton: Float = 1f
): Double {
    // TODO: Consider extracting my own values for pi and e so I don't have to rely on Java libs
    return (1f / (standardDeviaton * sqrt(2f * Math.PI))) *
            (Math.E.pow(((-1f / 2f) * ((input - mean) / standardDeviaton).pow(2)).toDouble()))
}

typealias ProbabilityMap<K> = Map<K, Float>

data class WeightedProbabilityMap<K>(
    val map: ProbabilityMap<K>,
    val weight: Float
)
