package dev.bryanlindsey.staccato

import dev.bryanlindsey.themecomposer.Composition
import org.jfugue.midi.MidiFileManager
import java.io.File

fun writeCompositionToMidi(
    composition: Composition,
    includeChords: Boolean,
    saveDirectory: File,
    fileName: String,
): File {
    val pattern = composition.convertToPattern(
        includeChords = false,
        includeMetronome = false,
    )

    val file = File(saveDirectory, fileName)
    MidiFileManager.savePatternToMidi(pattern, file)

    return file
}
