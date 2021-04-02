package dev.bryanlindsey.musicgenerator3.ui.saved

import dev.bryanlindsey.musicgenerator3.AppViewModel
import dev.bryanlindsey.musicgenerator3.CompositionRepository
import dev.bryanlindsey.musicgenerator3.SavedComposition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedCompositionViewModel @Inject constructor(
    private val repository: CompositionRepository
) : AppViewModel() {

    val compositionsState = MutableStateFlow<List<SavedComposition>>(emptyList())

    val selectedComposition = MutableStateFlow<SavedComposition?>(null)

    init {
        viewModelScope.launch {
            repository.getAllCompositionsFlow().collect { compositionList ->
                compositionsState.value = compositionList
                val currentSelection = selectedComposition.value
                if (currentSelection != null) {
                    val updatedSelection = compositionList.find { it.id == currentSelection.id }
                    selectComposition(updatedSelection)
                }
            }
        }
    }

    fun deleteComposition(composition: SavedComposition) {
        viewModelScope.launch {
            repository.deleteComposition(composition)
        }
    }

    fun selectComposition(composition: SavedComposition?) {
        selectedComposition.value = composition
    }

    fun updateComposition(updatedComposition: SavedComposition) {
        viewModelScope.launch {
            repository.updateComposition(updatedComposition)
        }
    }
}
