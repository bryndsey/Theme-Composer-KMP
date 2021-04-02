package dev.bryanlindsey.composer

import dev.bryanlindsey.composer.musicparts.*
import dev.bryanlindsey.composer.postprocessor.CompositionPostProcessor
import kotlin.random.Random

fun main() {
    val composition = Composer.compose(
        keyPitch = Pitch.C,
        keyScaleType = ScaleType.MAJOR,
        timeSignature = TimeSignature.THREE_FOUR
    )
    println("Key: ${composition.key.pitch} ${composition.key.scaleType}")
    println("Time Signature: ${composition.timeSignature}")
    println("Structure: ${composition.structure}")
    composition.sectionList.forEach { section ->
        println("--- SECTION ${section.name} ---")
        section.measures.forEach {
            println(it.chordRoot)
            println(it.noteList)
        }
    }
}

object Composer {

    private val measureCountProbabilityMap = ProbabilityMap(
        1 to 0.25f,
        2 to 0.5f,
        3 to 0.1f,
        4 to 5f,
        8 to 1f
    )

    private val timeSignatureProbabilityMap = ProbabilityMap(
        TimeSignature.FOUR_FOUR to 1f,
        TimeSignature.THREE_FOUR to 0.85f,
        TimeSignature.TWO_FOUR to 0.3f,
        TimeSignature.SIX_EIGHT to 0.1f
    )

    fun compose(
        keyPitch: Pitch? = null,
        keyScaleType: ScaleType? = null,
        timeSignature: TimeSignature? = null,
        tempoBPM: Int? = null,
        chordProgressionSections: List<List<ScaleDegree>>? = null
    ): Composition {
        val actualKeyPitch = keyPitch ?: generateKeyPitch()
        val actualKeyScaleType = keyScaleType ?: generateKeyScaleType()
        val key = Key(actualKeyPitch, actualKeyScaleType)

        val actualTimeSignature = timeSignature ?: generateTimeSignature()

        val actualTempoBPM = tempoBPM ?: generateTempo()

        val subdivisionType = RhythmGenerator.SubdivisionType.values().random()
        println("Subdivision type = $subdivisionType")

        val subdivisionProbabilityFactor = RhythmGenerator.subdivisionProbabilityFactors.random()
        println("Subdivision probability factor = $subdivisionProbabilityFactor")

        val sections = if (chordProgressionSections.isNullOrEmpty()) {
            generateSections(
                actualTimeSignature.beatsPerMeasure,
                subdivisionType,
                subdivisionProbabilityFactor,
                actualKeyScaleType
            )
        } else {
            generateSectionsFromProgression(
                chordProgressionSections,
                actualTimeSignature.beatsPerMeasure,
                subdivisionType,
                subdivisionProbabilityFactor
            )
        }

        return Composition(
            key = key,
            timeSignature = actualTimeSignature,
            tempoBPM = actualTempoBPM,
            sectionList = sections,
            structure = StructureGenerator.generateStructure(sections)
        )
    }

    private fun generateKeyPitch() = Pitch.values().random()
    private fun generateKeyScaleType() = ScaleType.values().random()

    private fun generateTimeSignature() = timeSignatureProbabilityMap.getRandomItem()

    private fun generateTempo() = Random.nextInt(50, 220)

    private fun generateSections(
        beatsPerMeasure: Int,
        subdivisionType: RhythmGenerator.SubdivisionType,
        subdivisionProbabilityFactor: Float,
        scaleType: ScaleType
    ): List<Section> {
        val numberOfUniqueSections = Random.nextInt(2, 4)
        val chordLikelihoods = ChordLikelihoodLimitGenerator.generateChordLikelihoodLimits()
        return (0 until numberOfUniqueSections).map {
            val chordProgressionLength = measureCountProbabilityMap.getRandomItem()
            generateSection(
                it.toString(),
                chordProgressionLength,
                beatsPerMeasure,
                subdivisionType,
                subdivisionProbabilityFactor,
                scaleType,
                chordLikelihoods
            )
        }
    }

    private fun generateSection(
        name: String,
        measureCount: Int,
        beatsPerMeasure: Int,
        subdivisionType: RhythmGenerator.SubdivisionType,
        subdivisionProbabilityFactor: Float,
        scaleType: ScaleType,
        chordLikelihoods: ChordProgressionGenerator.ChordTransitionProbability
    ): Section {
        val sectionChords =
            ChordProgressionGenerator.generateChords(measureCount, scaleType, chordLikelihoods)

        return Section(
            name,
            generateMeasures(
                beatsPerMeasure = beatsPerMeasure,
                subdivisionType = subdivisionType,
                subdivisionProbabilityFactor = subdivisionProbabilityFactor,
                chordProgression = sectionChords
            )
        )
    }

    private fun generateSectionsFromProgression(
        progressionSections: List<List<ScaleDegree>>,
        beatsPerMeasure: Int,
        subdivisionType: RhythmGenerator.SubdivisionType,
        subdivisionProbabilityFactor: Float
    ): List<Section> {
        return progressionSections.mapIndexed { index, progression ->
            generateSectionFromProgression(
                name = index.toString(),
                progression = progression,
                beatsPerMeasure = beatsPerMeasure,
                subdivisionType = subdivisionType,
                subdivisionProbabilityFactor = subdivisionProbabilityFactor
            )
        }
    }

    private fun generateSectionFromProgression(
        name: String,
        progression: List<ScaleDegree>,
        beatsPerMeasure: Int,
        subdivisionType: RhythmGenerator.SubdivisionType,
        subdivisionProbabilityFactor: Float
    ): Section {
        return Section(
            name,
            generateMeasures(
                beatsPerMeasure = beatsPerMeasure,
                subdivisionType = subdivisionType,
                subdivisionProbabilityFactor = subdivisionProbabilityFactor,
                chordProgression = progression
            )
        )
    }

    private fun generateMeasures(
        beatsPerMeasure: Int,
        subdivisionType: RhythmGenerator.SubdivisionType,
        subdivisionProbabilityFactor: Float,
        chordProgression: List<ScaleDegree>
    ): List<Measure> {
        val measureCount = chordProgression.size

        val melodyTimings = (0 until measureCount).map {
            RhythmGenerator.createRhythm(
                numberOfBeats = beatsPerMeasure,
                subdivisionsType = subdivisionType,
                subdivisionProbabilityFactor = subdivisionProbabilityFactor
            )
        }
        val melodyNotes = melodyTimings.map { MelodyGenerator.generateMelody(it) }

        return chordProgression.zip(melodyNotes) { chord, notes ->
            Measure(chord, notes)
        }
    }
}

fun Composition.applyPostProcessor(postProcessor: CompositionPostProcessor) =
    postProcessor.run(this)

data class Composition(
    val key: Key,
    val timeSignature: TimeSignature,
    val tempoBPM: Int,
    val sectionList: List<Section>,
    val structure: List<String>
)

data class Section(
    val name: String,
    val measures: List<Measure>
)
