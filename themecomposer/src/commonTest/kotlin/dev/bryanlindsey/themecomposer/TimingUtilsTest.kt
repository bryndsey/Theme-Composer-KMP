package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.NoteTiming
import dev.bryanlindsey.themecomposer.structure.TimeSignature
import org.junit.Assert.assertEquals
import org.junit.Test

internal class TimingUtilsTest {

    @Test
    fun `generates rhythm as expected`() {
        val eightNoteProbabilities = listOf(
            1f, 0f, 0f, 1f, 0f, 0f, 0f, 1f
        )
        val expectedNoteTimings = listOf(
            NoteTiming(0, 3),
            NoteTiming(3, 4),
            NoteTiming(7, 1),
        )
        val actualNoteTimings = generateRhythm(TimeSignature.FOUR_FOUR, 4, 0, eightNoteProbabilities)

        assertEquals(expectedNoteTimings, actualNoteTimings)
    }

    @Test
    fun `nonzero start beat generates rhythm as expected`() {
        val eightNoteProbabilities = listOf(
            1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f
        )
        val expectedNoteTimings = listOf(
            NoteTiming(2, 1),
            NoteTiming(3, 3),
            NoteTiming(6, 1),
            NoteTiming(7, 1),
        )
        val actualNoteTimings = generateRhythm(TimeSignature.FOUR_FOUR, 3, 1, eightNoteProbabilities)

        assertEquals(expectedNoteTimings, actualNoteTimings)
    }

    @Test
    fun `more beats than probabilities wraps around probabilities and generates rhythm as expected`() {
        val eightNoteProbabilities = listOf(
            1f, 0f
        )
        val expectedNoteTimings = listOf(
            NoteTiming(0, 2),
            NoteTiming(2, 2),
            NoteTiming(4, 2),
            NoteTiming(6, 2)
        )
        val actualNoteTimings = generateRhythm(TimeSignature.FOUR_FOUR, 4, 0, eightNoteProbabilities)

        assertEquals(expectedNoteTimings, actualNoteTimings)
    }

    @Test
    fun `complex inputs generates rhythm as expected`() {
        val eightNoteProbabilities = listOf(
            1f, 0f, 1f, 1f, 0f
        )
        val expectedNoteTimings = listOf(
            NoteTiming(2, 1),
            NoteTiming(3, 2),
            NoteTiming(5, 2),
            NoteTiming(7, 1),
            NoteTiming(8, 2),
            NoteTiming(10, 2),
            NoteTiming(12, 1),
            NoteTiming(13, 2),
            NoteTiming(15, 1)
        )
        val actualNoteTimings = generateRhythm(TimeSignature.FOUR_FOUR, 7, 1, eightNoteProbabilities)

        assertEquals(expectedNoteTimings, actualNoteTimings)
    }
}
