package dev.bryanlindsey.themecomposer

import dev.bryanlindsey.themecomposer.structure.NamedPitch
import dev.bryanlindsey.themecomposer.structure.SpecificPitch
import kotlin.random.Random

private fun getDefaultRangeCenter() = SpecificPitch(NamedPitch.values().random(), Random.nextInt(3, 6))
private fun getRangeSpreadFactor() = Random.nextFloat() * 4f + 4.5f
private fun getStepwiseSpreadFactor() = Random.nextFloat(2f, 4f)
private fun getMainNoteLikelihoodScalingFactor() = Random.nextFloat()// 0.9f

private fun getRhythmRepeatChance() = Random.nextFloat() * 0.75f + 0.25f//0.5f

private fun getDiatonicNoteProbabilityFactor() = 1f
private fun getNonDiatonicNoteProbabilityFactor() =
    if (isRandomCheckSuccessful(0.6f)) {
        0f
    } else {
        Random.nextFloat() * 0.1f
    }

private fun getChordToneProbabilityFactor() = 1f
private fun getNonChordToneProbabilityFactor() = //0.25f
    if (isRandomCheckSuccessful(0.4f)) {
        0f
    } else {
        Random.nextFloat() * 0.5f
    }

private fun getPrimaryDownbeatNoteStartProbability() = Random.nextFloat(0.8f, 1f)
private fun getSecondaryDownbeatNoteStartProbability() = Random.nextFloat(0.7f, 0.9f)
private fun getBackbeatNoteStartProbability() = Random.nextFloat(0.125f, 0.7f)

private fun getThreeFourRhythmProbabilities() = threeFourEighthNoteStartLikelihoods.random()
private fun getFourFourRhythmProbabilities() = fourFourEighthNoteStartLikelihoods.random()


var DEFAULT_RANGE_CENTER = getDefaultRangeCenter()
var RANGE_SPREAD_FACTOR = getRangeSpreadFactor()
var STEPWISE_SPREAD_FACTOR = getStepwiseSpreadFactor()
var MAIN_NOTE_LIKELIHOOD_SCALING_FACTOR = getMainNoteLikelihoodScalingFactor()

var RHYTHM_REPEAT_CHANCE = getRhythmRepeatChance()

var DIATONIC_NOTE_PROBABILITY_FACTOR = getDiatonicNoteProbabilityFactor()
var NON_DIATONIC_NOTE_PROBABILITY_FACTOR = getNonDiatonicNoteProbabilityFactor()

var CHORD_TONE_PROBABILITY_FACTOR = getChordToneProbabilityFactor()
var NON_CHORD_TONE_PROBABILITY_FACTOR = getNonChordToneProbabilityFactor()

var PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY = getPrimaryDownbeatNoteStartProbability()
var SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY = getSecondaryDownbeatNoteStartProbability()
var BACKBEAT_NOTE_START_PROBABILITY = getBackbeatNoteStartProbability()

var THREE_FOUR_RHYTHM_PROBABILITIES = getThreeFourRhythmProbabilities()
var FOUR_FOUR_RHYTHM_PROBABILITIES = getFourFourRhythmProbabilities()

fun randomizeParameters() {
    DEFAULT_RANGE_CENTER = getDefaultRangeCenter()
    RANGE_SPREAD_FACTOR = getRangeSpreadFactor()
    STEPWISE_SPREAD_FACTOR = getStepwiseSpreadFactor()
    MAIN_NOTE_LIKELIHOOD_SCALING_FACTOR = getMainNoteLikelihoodScalingFactor()
    RHYTHM_REPEAT_CHANCE = getRhythmRepeatChance()
    DIATONIC_NOTE_PROBABILITY_FACTOR = getDiatonicNoteProbabilityFactor()
    NON_DIATONIC_NOTE_PROBABILITY_FACTOR = getNonDiatonicNoteProbabilityFactor()
    CHORD_TONE_PROBABILITY_FACTOR = getChordToneProbabilityFactor()
    NON_CHORD_TONE_PROBABILITY_FACTOR = getNonChordToneProbabilityFactor()
    PRIMARY_DOWNBEAT_NOTE_START_PROBABILITY = getPrimaryDownbeatNoteStartProbability()
    SECONDARY_DOWNBEAT_NOTE_START_PROBABILITY = getSecondaryDownbeatNoteStartProbability()
    BACKBEAT_NOTE_START_PROBABILITY = getBackbeatNoteStartProbability()
    THREE_FOUR_RHYTHM_PROBABILITIES = getThreeFourRhythmProbabilities()
    FOUR_FOUR_RHYTHM_PROBABILITIES = getFourFourRhythmProbabilities()
}
