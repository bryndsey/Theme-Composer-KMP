import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.player.CompositionPlayer
import dev.bryanlindsey.musicgenerator3.ui.common.CompositionDisplay
import dev.bryanlindsey.musicgenerator3.ui.theme.MusicGenerator3Theme
import dev.bryanlindsey.themecomposer.Composition
import dev.bryanlindsey.themecomposer.composeTheme
import dev.bryanlindsey.themecomposer.generateRandomProperties
import kotlinx.coroutines.launch

fun main() = Window {
    val coroutineScope = rememberCoroutineScope()

    val compositionPlayer = CompositionPlayer()

    var composition by remember {
        val initialComposition = getNewComposition()
        mutableStateOf(initialComposition)
    }

    MusicGenerator3Theme {
        Surface {
            Column {
                CompositionDisplay(
                    modifier = Modifier.weight(1f),
                    composition = composition
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                compositionPlayer.playComposition(
                                    composition
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.PlayArrow, "Play")
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                compositionPlayer.stop()
                                composition = getNewComposition()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Refresh, "Generate")
                    }
                }
            }
        }
    }
}

fun getNewComposition(): Composition {
    val properties = generateRandomProperties()

    return composeTheme(
        properties.tempo,
        properties.timeSignature,
        properties.scale,
        properties.chordProgression,
        properties.melodyInstrument,
        properties.chordInstrument,
    )
}