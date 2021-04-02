package dev.bryanlindsey.composer.musicparts

sealed class Note {
    abstract val noteTiming: NoteTiming

    // TODO: Rename to "ChordRelativePitchNote"
    data class RelativePitchNote(
        val intervalDegree: ScaleDegree,
        override val noteTiming: NoteTiming
    ) : Note()

    data class Rest(override val noteTiming: NoteTiming) : Note()

    data class ScaleRelativePitchNote(
        val semitonesAboveScaleRoot: Int,
        override val noteTiming: NoteTiming
    ) : Note()
}

fun Note.RelativePitchNote.toScaleRelativePitch(
    referencePitchScaleDegree: ScaleDegree,
    scaleType: ScaleType
): Note.ScaleRelativePitchNote {

    val noteScaleIndexRelativeToBase = intervalDegree.index

    val referencePitchScaleIndex = referencePitchScaleDegree.index
    val scaleIntervalSemitonesList = scaleType.intervalSemitonesList

    val noteAbsoluteScaleIndex =
        (referencePitchScaleIndex + noteScaleIndexRelativeToBase) % scaleIntervalSemitonesList.size
    val normalizedNoteSemitonesFromScaleRoot = scaleIntervalSemitonesList[noteAbsoluteScaleIndex]

    val octavesAboveScaleRoot = ((referencePitchScaleIndex + noteScaleIndexRelativeToBase) / scaleIntervalSemitonesList.size)
    val octaveOffsetSemitones = octavesAboveScaleRoot * Pitch.values().size

    return Note.ScaleRelativePitchNote(
        normalizedNoteSemitonesFromScaleRoot + octaveOffsetSemitones,
        noteTiming
    )
}
