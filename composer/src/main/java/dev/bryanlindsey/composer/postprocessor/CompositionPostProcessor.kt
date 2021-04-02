package dev.bryanlindsey.composer.postprocessor

import dev.bryanlindsey.composer.Composition

interface CompositionPostProcessor {

    fun run(composition: Composition): Composition
}
