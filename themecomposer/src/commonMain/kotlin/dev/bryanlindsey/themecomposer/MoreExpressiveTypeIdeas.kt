package dev.bryanlindsey.themecomposer

/**
 * This is a place where I was gonna start dumping some ideas for more expressive types.
 * I don't want to get too bogged down in them right now, but I don't want to lose track of my
 * ideas.
 *
 * Also, I'm mostly making them typealiases right now, but they might actually be better as full
 * types. It's just quicker to show the replacement while I'm trying to get my thoughts down
 */

// The idea is that this is a float between 0 and 1. Not sure if there is a more expressive name
typealias ProbabilityPercentage = Float

// Might want some types to represent things like melodies and rhythms, as well as full compositions
// I'm hesitant to do that right now since these things might change form and I don't want to have
// to worry about a lot of refactoring old code every time I change a typealias. Maybe once things
// settle in more I can do that though.
