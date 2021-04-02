package dev.bryanlindsey.musicgenerator3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AppViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main)
}
