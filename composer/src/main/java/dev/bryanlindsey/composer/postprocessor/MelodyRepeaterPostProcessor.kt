package dev.bryanlindsey.composer.postprocessor

import dev.bryanlindsey.composer.Composition
import dev.bryanlindsey.composer.musicparts.Measure
import dev.bryanlindsey.composer.musicparts.ScaleType
import kotlin.random.Random

class MelodyRepeaterPostProcessor : CompositionPostProcessor {

    enum class RepeatPatterns(val subsectionIdexList: List<Int>) {
        AAAA(listOf(0, 0, 0, 0)),
        AAAB(listOf(0, 0, 0, 1)),
        AABA(listOf(0, 0, 1, 0)),
        AABB(listOf(0, 0, 1, 1)),
        ABAA(listOf(0, 1, 0, 0)),
        ABAB(listOf(0, 1, 0, 1)),
        ABAC(listOf(0, 1, 0, 2)),
        ABBA(listOf(0, 1, 1, 0)),
        ABBB(listOf(0, 1, 1, 1)),
        ABBC(listOf(0, 1, 1, 2)),
        ABCA(listOf(0, 1, 2, 0)),
        ABCB(listOf(0, 1, 2, 1)),
        ABCC(listOf(0, 1, 2, 2))
    }

    // TODO: It would be kinda nice to have this extracted directly to repeatPatterns
    private val subsectionsInPatterns = 4

    override fun run(composition: Composition): Composition {
        val sectionMeasures = composition.sectionList.map { section ->
            section.measures
        }

        val updatedMelodies = sectionMeasures.map {
            adjustMelodyForSection(it, composition.key.scaleType)
        }

        val updatedSections = composition.sectionList.zip(updatedMelodies) {section, measures ->
            section.copy(measures = measures)
        }

        return composition.copy(sectionList = updatedSections)
    }

    private fun adjustMelodyForSection(measures: List<Measure>, scaleType: ScaleType): List<Measure> {
        println("Checking notes for new section")

        val measuresCopy = measures.toMutableList()

        val useRepeatPatternProbability = 0.8f

        if (measures.size >= subsectionsInPatterns && Random.nextFloat() < useRepeatPatternProbability) {
            val chosenPattern = RepeatPatterns.values().random()
            println("Chose pattern $chosenPattern")

            val measuresToRepeat = measures.size / subsectionsInPatterns

            val measureNoteChunks = measures.map { it.noteList }.chunked(measuresToRepeat)
            val newMeasureNotes = chosenPattern.subsectionIdexList.flatMap {  measureIndex ->
                measureNoteChunks[measureIndex]
            }

            newMeasureNotes.forEachIndexed { index, noteList ->
                measuresCopy[index] = measuresCopy[index].copy(noteList = noteList)
            }
        }

        return measuresCopy
    }
}
