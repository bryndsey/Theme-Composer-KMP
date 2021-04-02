package dev.bryanlindsey.musicgenerator3.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.BackdropNavigationGraph
import dev.bryanlindsey.musicgenerator3.ui.BackdropNavigator
import dev.bryanlindsey.musicgenerator3.ui.theme.MusicGenerator3Theme
import kotlinx.coroutines.flow.collect


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App(
    backdropNavigator: BackdropNavigator,
    backdropNavigationGraph: BackdropNavigationGraph,
) {
    MusicGenerator3Theme(
//        darkTheme = false
        darkTheme = true
    ) {
        val backdropConfiguration = backdropNavigator.currentScreenFlow.collectAsState()

        val backdropScaffoldState = rememberBackdropScaffoldState(
            initialValue = BackdropValue.Revealed
        )

        val currentScreen = backdropConfiguration.value

        val currentScreenSet = remember(currentScreen) {
            backdropNavigationGraph.getContentForScreen(currentScreen)
        }

        val backgroundColor = MaterialTheme.colors.background

        BackdropScaffold(
            scaffoldState = backdropScaffoldState,
            appBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text(currentScreen.label) },
                    actions = when (backdropScaffoldState.targetValue) {
                        BackdropValue.Concealed -> currentScreenSet.frontTopBarOptions
                        BackdropValue.Revealed -> currentScreenSet.backTopBarOptions
                    },
                    navigationIcon =
                    if (currentScreen != Screen.HomeNavigation) {
                        {
                            IconButton(onClick = { backdropNavigator.navigate(Screen.HomeNavigation) }) {
                                Icon(Icons.Default.Menu, "Menu")
                            }
                        }
                    } else null,
                    elevation = 0.dp,
                    backgroundColor = backgroundColor
                )
            },
            backLayerContent = {
                Crossfade(targetState = currentScreenSet) {
                    it.backScreen()
                }
            },
            frontLayerContent = {
                Crossfade(targetState = currentScreenSet) {
                    it.frontScreen()
                }
            },
            backLayerBackgroundColor = backgroundColor,
            // This is kind of a hack to fix a crash, though maybe I'll actually keep it?
            stickyFrontLayer = false
        )

        LaunchedEffect(key1 = currentScreenSet) {
            when (currentScreenSet.defaultFocusedScreen) {
                BackdropFocusedScreen.FRONT -> backdropNavigator.setBackdropValue(BackdropValue.Concealed)
                BackdropFocusedScreen.BACK -> backdropNavigator.setBackdropValue(BackdropValue.Revealed)
            }
        }

        LaunchedEffect(key1 = backdropNavigator) {
            backdropNavigator.backgroundStateUpdates.collect {
                if (backdropScaffoldState.targetValue != it) {
                    when (it) {
                        BackdropValue.Concealed -> backdropScaffoldState.conceal()
                        BackdropValue.Revealed -> backdropScaffoldState.reveal()
                    }
                }
            }
        }
    }
}
