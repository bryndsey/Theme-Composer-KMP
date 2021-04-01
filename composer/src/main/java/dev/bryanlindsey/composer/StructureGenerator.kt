package dev.bryanlindsey.composer

object StructureGenerator {

    private val sectionRepeatsTimesProbabilityMap = ProbabilityMap(
        1 to 0.5f,
        2 to 2f,
        3 to 2f,
        4 to 0.75f
    )

    fun generateStructure(sectionList: List<Section>) =
        sectionList
            .flatMap { section ->
                // Determine number of times to repeat section
                val sectionRepeatTimes = sectionRepeatsTimesProbabilityMap.getRandomItem()

                (1..sectionRepeatTimes).map { section.name }
            }
            .shuffled()
}
