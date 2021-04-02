package dev.bryanlindsey.musicgenerator3.ui.generation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.ui.common.DropdownList
import dev.bryanlindsey.themecomposer.MidiInstrument
import dev.bryanlindsey.themecomposer.getDisplayName
import dev.bryanlindsey.themecomposer.structure.ChordType
import dev.bryanlindsey.themecomposer.structure.NamedPitch
import dev.bryanlindsey.themecomposer.structure.ScaleType
import dev.bryanlindsey.themecomposer.structure.TimeSignature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.roundToInt


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CompositionParameterControls(
    viewModel: GenerationViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val parametersState = viewModel.parameterState.collectAsState()
        val parameters = parametersState.value
        val timeSignature = parameters.timeSignature
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            RandomizableSetting(
                isRandomized = parameters.isTimeSignatureRandom,
                onRandomizedSettingChanged = { viewModel.setIsTimeSignatureRandom(it) },
                settingName = "Time Signature",
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RadioButton(
                        selected = timeSignature == TimeSignature.THREE_FOUR,
                        onClick = { viewModel.setTimeSignature(TimeSignature.THREE_FOUR) },
                    )
                    Text(text = "3/4")
                    Spacer(modifier = Modifier.width(4.dp))
                    RadioButton(
                        selected = timeSignature == TimeSignature.FOUR_FOUR,
                        onClick = { viewModel.setTimeSignature(TimeSignature.FOUR_FOUR) },
                    )
                    Text(text = "4/4")
                }
            }

            Divider()

            val tempoRange = 60f..200f
            RandomizableSetting(
                isRandomized = parameters.isTempoRandom,
                onRandomizedSettingChanged = { viewModel.setIsTempoRandom(it) },
                settingName = "Tempo",
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        modifier = Modifier.weight(1f),
                        value = parameters.tempo.toFloat(),
                        onValueChange = {
                            val newTempo = it.roundToInt()
                            viewModel.setTempo(newTempo)
                        },
                        valueRange = tempoRange,
//                thumbColor = MaterialTheme.colors.secondary,
//                activeTrackColor = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = "${parameters.tempo} BPM",
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(72.dp)
                    )
                }
            }

            Divider()

            RandomizableSetting(
                isRandomized = parameters.isScaleRandom,
                onRandomizedSettingChanged = { viewModel.setIsScaleRandom(it) },
                settingName = "Scale",
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DropdownList(
                        listItems = NamedPitch.values().asList(),
                        onItemSelected = { viewModel.setScalePitch(it) },
                        selectedItem = parameters.key.rootPitch,
                        getDisplayString = { it.getDisplayName() },
                        modifier = Modifier.width(80.dp)
                    )
                    DropdownList(
                        listItems = ScaleType.values().asList(),
                        onItemSelected = { viewModel.setScaleType(it) },
                        selectedItem = parameters.key.type,
                        getDisplayString = { it.getDisplayName() }
                    )
                }
            }

            Divider()

            RandomizableSetting(
                isRandomized = parameters.isChordRandom,
                onRandomizedSettingChanged = { viewModel.setIsChordRandom(it) },
                settingName = "Chord Progression"
            ) {
                ChordProgressionInput(
                    chordProgression = parameters.chordProgression,
                    onUpdateProgression = { viewModel.setChordProgression(it) })
            }

            Divider()

            RandomizableSetting(
                isRandomized = parameters.isMelodyInstrumentRandom,
                onRandomizedSettingChanged = { viewModel.setIsMelodyInstrumentRandom(it) },
                settingName = "Melody Instrument"
            ) {
                DropdownList(
                    listItems = MidiInstrument.values().asList(),
                    onItemSelected = { viewModel.setMelodyInstrument(it) },
                    selectedItem = parameters.melodyInstrument,
                    getDisplayString = { it.getDisplayName() },
                    modifier = Modifier.widthIn(225.dp),
                )
            }

            Divider()

            RandomizableSetting(
                isRandomized = parameters.isChordInstrumentRandom,
                onRandomizedSettingChanged = { viewModel.setIsChordInstrumentRandom(it) },
                settingName = "Chord Instrument"
            ) {
                DropdownList(
                    listItems = MidiInstrument.values().asList(),
                    onItemSelected = { viewModel.setChordInstrument(it) },
                    selectedItem = parameters.chordInstrument,
                    getDisplayString = { it.getDisplayName() },
                    modifier = Modifier.widthIn(225.dp),
                )
            }

            Divider()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RandomizableSetting(
    isRandomized: Boolean,
    onRandomizedSettingChanged: (Boolean) -> Unit,
    settingName: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isUserSet = !isRandomized

    Column(modifier = modifier.then(Modifier.padding(8.dp))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val toggleIconAlpha = 0.75f

            Text(
                text = settingName,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.Shuffle,
                contentDescription = null,
                modifier = Modifier.alpha(toggleIconAlpha),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Switch(
                checked = isUserSet,
                onCheckedChange = { onRandomizedSettingChanged(isUserSet) })
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.alpha(toggleIconAlpha),
            )
        }

        AnimatedVisibility(
            visible = isUserSet,
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
fun ChordSelectionControl(
    chordRootPitch: NamedPitch,
    onChordRootPitchChanged: (NamedPitch) -> Unit,
    chordType: ChordType,
    onChordTypeChanged: (ChordType) -> Unit,
    enabled: Boolean = true,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DropdownList(
            listItems = NamedPitch.values().asList(),
            onItemSelected = onChordRootPitchChanged,
            selectedItem = chordRootPitch,
            enabled = enabled,
            getDisplayString = { it.getDisplayName() },
            modifier = Modifier.width(80.dp)
        )
        DropdownList(
            listItems = ChordType.values().asList(),
            onItemSelected = onChordTypeChanged,
            selectedItem = chordType,
            enabled = enabled,
            getDisplayString = { it.getDisplayName() }
        )
    }
}
