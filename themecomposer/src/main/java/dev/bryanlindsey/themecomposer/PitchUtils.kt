package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.*

fun getShortestSemitoneDistanceBetweenSemitoneOffsetsFromC(from: Float, to: Float): Float {
    val totalNumberOfSemitones = NamedPitch.values().size

    val rawDistance = to - from
    val distance = if (rawDistance > totalNumberOfSemitones / 2) {
        rawDistance - totalNumberOfSemitones
    } else if (rawDistance < totalNumberOfSemitones / -2) {
        rawDistance + totalNumberOfSemitones
    } else {
        rawDistance
    }

    return distance
}

fun getSemitoneDistanceBetween(from: SpecificPitch, to: SpecificPitch): Int {
    val fromRelativePitch = from.octave * SEMITONES_PER_OCTAVE + from.namedPitch.semitoneOffsetCountFromC
    val toRelativePitch = to.octave * SEMITONES_PER_OCTAVE + to.namedPitch.semitoneOffsetCountFromC

    return toRelativePitch - fromRelativePitch
}

fun SpecificPitch.getSemitoneOffsetFromMiddleC(): Int =
    if (this == MIDDLE_C) {
        0
    } else {
        val octaveOffset = this.octave - MIDDLE_C.octave

        octaveOffset * SEMITONES_PER_OCTAVE + this.namedPitch.semitoneOffsetCountFromC
    }
