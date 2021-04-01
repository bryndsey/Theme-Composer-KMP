package dev.bryanlindsey.themecomposer.worksheets

import dev.bryanlindsey.themecomposer.WeightedProbabilityMap
import dev.bryanlindsey.themecomposer.combineProbabilitiesWithWeights
import dev.bryanlindsey.themecomposer.combineWith
import dev.bryanlindsey.themecomposer.normalizeProbabilities

val map1 = mapOf(
    0 to 1f,
    1 to 0.5f,
    2 to 0.75f,
    3 to 1f,
    4 to 0f,
    5 to .2f
)

val map2 = mapOf(
    1 to 0.25f,
    2 to 1f,
    3 to 0.75f,
    4 to 1f,
    5 to .4f
)

println("Map 1: $map1")
println("Map 2: $map2")

//    val equalWeightsMap = map1.combineWith(map2)
//    println("\nEqual weights 1: ${equalWeightsMap}\nNormalized: ${equalWeightsMap.normalizeProbabilities()}")
//    println("Equal weights 2: ${combineProbabilityMapsWithWeights(map1, 2f, map2, 2f)}")
//    println("Pre-and-post normalized 1: ${map1.normalizeProbabilities().combineWith(map2.normalizeProbabilities()).normalizeProbabilities()}")
//    println("Pre-and-post normalized 2: ${combineProbabilityMapsWithWeights(map1.normalizeProbabilities(), 1f, map2.normalizeProbabilities(), 1f).normalizeProbabilities()}")
//
//    val map1DoubledWeightsMap = combineProbabilityMapsWithWeights(map1, 2f, map2, 1f)
//    println("\nMap 1 doubled weights: ${map1DoubledWeightsMap}\nNormalized: ${map1DoubledWeightsMap.normalizeProbabilities()}")
//
//    val map2HalvedWeightsMap = combineProbabilityMapsWithWeights(map1, 1f, map2, 0.5f)
//    println("\nMap 2 halved weights: ${map2HalvedWeightsMap}\nNormalized: ${map2HalvedWeightsMap.normalizeProbabilities()}")
//
//    val map1HalvedWeightsMap = combineProbabilityMapsWithWeights(map1, 0.5f, map2, 1f)
//    println("\nMap 1 halved weights: ${map1HalvedWeightsMap}\nNormalized: ${map1HalvedWeightsMap.normalizeProbabilities()}")

val map3 = mapOf(
    1 to 0.2125f,
    2 to 0.66f,
    3 to 0.35f,
    4 to 0.33f,
    5 to 0.25f,
    6 to 0f
)
println("Map 3: $map3")

//    val firstCombine1 = combineProbabilityMapsWithWeights(map1, 2f, map2, 4f)
//    val secondCombine1 = combineProbabilityMapsWithWeights(firstCombine1, 3f, map3, 2f)//.normalizeProbabilities()
//
//    println("\n\nFirst direction: $secondCombine1")
//
//    val firstCombine2 = combineProbabilityMapsWithWeights(map2, 4f, map3, 2f)
//    val secondCombine2 = combineProbabilityMapsWithWeights(map1, 2f, firstCombine2, 3f)//.normalizeProbabilities()
//
//    println("Second direction: $secondCombine2")
//
//    val oneAnd2 = combineProbabilityMapsWithWeights(map1, 1f, map2, 20f)
//    val oneAnd3 = combineProbabilityMapsWithWeights(map1, 1f, map3, 3f)
//    val twoAnd3 = combineProbabilityMapsWithWeights(map2, 20f, map3, 3f)
//
//    println(oneAnd2)
//    println(oneAnd3)
//    println(twoAnd3)
//
//    val averaged = (1..5).map {
//        it to (map1[it]!!.pow(0.01f) * map2[it]!!.pow(0.01f) * map3.normalizeProbabilities()[it]!!.pow(50)).pow(1f/50.02f)
//    }.toMap().normalizeProbabilities()
//
//
//    println("Combined: $averaged")
//    println("3 normalized: ${map3.normalizeProbabilities()}")

val newThing = combineProbabilitiesWithWeights(
    WeightedProbabilityMap(map1, .1f),
    WeightedProbabilityMap(map2, .1f),
    WeightedProbabilityMap(map3, 1f),
)

println("Map1 normalized: ${map1.normalizeProbabilities()}")
println("Map3 normalized: ${map3.normalizeProbabilities()}")
println("New combine method: ${newThing.normalizeProbabilities()}")
println("Old combine method: ${(map1.combineWith(map2)).normalizeProbabilities()}")
