import dev.bryanlindsey.themecomposer.*
import dev.bryanlindsey.themecomposer.structure.*

val cScaleProbs = getScaleNoteProbabilities(
    Scale(NamedPitch.A, ScaleType.MINOR)
)
println(cScaleProbs)

val gChordProbs = getChordNoteProbabilities(
    Chord(NamedPitch.G, ChordType.MAJOR)
)
println(gChordProbs)

val result = combineProbabilitiesWithWeights(
    WeightedProbabilityMap(
        cScaleProbs,
        10f
    ),
    WeightedProbabilityMap(
        gChordProbs,
        1f
    )
)
val result2 = combineProbabilitiesWithWeights(
    WeightedProbabilityMap(
        cScaleProbs.normalizeProbabilities(),
        10f
    ),
    WeightedProbabilityMap(
        gChordProbs.normalizeProbabilities(),
        1f
    )
)

println(result)
println(result2)

println(cScaleProbs.combineWith(gChordProbs))
println(combineProbabilities(cScaleProbs, gChordProbs))

val rangeProbabilities = getSpecificPitchRangeProbabilitiesWithLimits(
    octaveCenterReference = DEFAULT_RANGE_CENTER,
    octaveSpreadFactor = 8f,
    lowerLimit = SpecificPitch(NamedPitch.B, 2),
    upperLimit = SpecificPitch(NamedPitch.A, 5)
)

val scaleRange = cScaleProbs
    .toSpecificPitchProbabilities()
    .combineWith(rangeProbabilities)

println(scaleRange.normalizeProbabilities().sort())

val chordScaleRange = gChordProbs
    .toSpecificPitchProbabilities()
    .combineWith(scaleRange)

println(chordScaleRange.normalizeProbabilities().sort())
