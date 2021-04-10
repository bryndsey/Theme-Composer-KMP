
import dev.bryanlindsey.themecomposer.normalizeProbabilities
import dev.bryanlindsey.themecomposer.perlinSequence
import kotlin.random.Random

val factor = Random.nextDouble()
println(factor)

val size = Random.nextDouble()
println(size)

perlinSequence(
    10,
    size,
    spreadFactor = factor,
    mapToMax = 10.0
)
    // The rest is to get it to a probability map format
    .map {
        it.toFloat()
    }
    .mapIndexed { index, value ->
        index to value
    }
    .toMap()
    .normalizeProbabilities()
    .onEach {
        println(it)
    }
