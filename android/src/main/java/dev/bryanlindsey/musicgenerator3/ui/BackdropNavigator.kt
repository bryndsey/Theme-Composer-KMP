package dev.bryanlindsey.musicgenerator3.ui


import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import dev.bryanlindsey.musicgenerator3.ui.main.BackdropScreenSet
import dev.bryanlindsey.musicgenerator3.ui.main.Screen
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalMaterialApi::class)
@Singleton
class BackdropNavigator @Inject constructor() {

    val currentScreenFlow = MutableStateFlow<Screen>(Screen.HomeNavigation)

    // I'm using a shared flow as opposed to state flow since I don't want the distinctUntilChanged
    // behavior - since the backdrop state can change outside of what is in this flow, I may need to
    // re-emit the same value to have the state updated properly
    // In that way, this is really more a stream of events than a long-running state
    private val mutableBackgroundStateUpdates: MutableSharedFlow<BackdropValue> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    val backgroundStateUpdates: Flow<BackdropValue> = mutableBackgroundStateUpdates

    fun navigate(screen: Screen) {
        currentScreenFlow.value = screen
    }

    fun setBackdropValue(backdropValue: BackdropValue) {
        mutableBackgroundStateUpdates.tryEmit(backdropValue)
    }
}

@OptIn(ExperimentalMaterialApi::class)
data class BackdropConfiguration(
    val screen: Screen,
    val backdropValue: BackdropValue
)

@Singleton
class BackdropNavigationGraph @Inject constructor(
    private val generationSet: BackdropScreenSet.GenerationSet,
    private val savedSet: BackdropScreenSet.SavedSet,
    private val homeSet: BackdropScreenSet.HomeNavigation
) {

    fun getContentForScreen(screen: Screen): BackdropScreenSet =
        when (screen) {
            Screen.Generation -> generationSet
            Screen.SavedCompositions -> savedSet
            Screen.HomeNavigation -> homeSet
        }
}
