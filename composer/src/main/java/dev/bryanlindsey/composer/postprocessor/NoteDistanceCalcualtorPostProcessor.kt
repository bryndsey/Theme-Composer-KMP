package dev.bryanlindsey.composer.postprocessor

import dev.bryanlindsey.composer.*
import dev.bryanlindsey.composer.musicparts.Measure
import dev.bryanlindsey.composer.musicparts.Note
import dev.bryanlindsey.composer.musicparts.ScaleType
import dev.bryanlindsey.composer.musicparts.toScaleRelativePitch

class NoteDistanceCalcualtorPostProcessor : CompositionPostProcessor {

    override fun run(composition: Composition): Composition {
        val sectionMeasures = composition.sectionList.map { section ->
            section.measures
        }

        val updatedMelodies = sectionMeasures.map {
            adjustMelodyForSection(it, composition.key.scaleType)
        }

        val updatedSections = composition.sectionList.zip(updatedMelodies) { section, measures ->
            section.copy(measures = measures)
        }

        return composition.copy(sectionList = updatedSections)
    }

    private fun adjustMelodyForSection(
        measures: List<Measure>,
        scaleType: ScaleType
    ): List<Measure> {
        println("Checking notes for new section")

        // TODO: This was some code I was playing with to determine note distance.
        //  Might still use it later, so leaving it for now.
        measures.flatMap { measure ->
            measure.noteList.map { note ->
                when (note) {
                    is Note.RelativePitchNote -> note.toScaleRelativePitch(
                        measure.chordRoot,
                        scaleType
                    ).semitonesAboveScaleRoot
                    is Note.Rest -> -1
                    is Note.ScaleRelativePitchNote -> note.semitonesAboveScaleRoot
                }
            }
        }
            .windowed(2) { midiIndexNotes ->
                val note1 = midiIndexNotes[0]
                val note2 = midiIndexNotes[1]
                if (note1 >= 0 && note2 >= 0) {
                    println("Note distance is ${note2 - note1} semitones")
                } else {
                    println("One of the notes is a rest")
                }
            }

        return measures
    }
}
