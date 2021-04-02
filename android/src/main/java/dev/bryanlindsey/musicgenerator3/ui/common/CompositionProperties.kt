package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.themecomposer.Composition
import dev.bryanlindsey.themecomposer.getDisplayName

@Composable
fun CompositionProperties(
    composition: Composition?,
    modifier: Modifier = Modifier,
    name: String? = null,
    orientation: Orientation = Orientation.Horizontal,
) {
    val placeholderCharacter = '-'

    val timeSignatureString =
        "${composition?.timeSignature?.beatsPerMeasure ?: placeholderCharacter}" +
                "/" +
                "${composition?.timeSignature?.beatDivisor ?: placeholderCharacter}"

    val scaleString =
        "${composition?.scale?.rootPitch?.getDisplayName() ?: placeholderCharacter}" +
                " " +
                "${composition?.scale?.type?.getDisplayName() ?: placeholderCharacter}"

    val tempoString = "${composition?.tempo ?: placeholderCharacter} BPM"

    if (orientation == Orientation.Horizontal) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeSignatureString,
                )
                Text(
                    text = scaleString,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = tempoString,
                )
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val horizontalPadding = 16.dp
            val horizontalPaddingModifier = Modifier.padding(horizontal = horizontalPadding)
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6,
                    modifier = horizontalPaddingModifier
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider()
            }

            Text(
                text = timeSignatureString,
                modifier = horizontalPaddingModifier
            )

            Divider(startIndent = horizontalPadding)

            Text(
                text = tempoString,
                modifier = horizontalPaddingModifier
            )

            Divider(startIndent = horizontalPadding)

            Text(
                text = scaleString,
                modifier = horizontalPaddingModifier
            )

            Divider(startIndent = horizontalPadding)

            Text(
                text = "Melody instrument",
                style = MaterialTheme.typography.caption,
                modifier = horizontalPaddingModifier
            )
            Text(
                text = composition?.melodyInstrument?.getDisplayName()
                    ?: placeholderCharacter.toString(),
                modifier = horizontalPaddingModifier
            )

            Divider(startIndent = horizontalPadding)

            Text(
                text = "Chord instrument",
                style = MaterialTheme.typography.caption,
                modifier = horizontalPaddingModifier
            )
            Text(
                text = composition?.chordInstrument?.getDisplayName()
                    ?: placeholderCharacter.toString(),
                modifier = horizontalPaddingModifier
            )

        }
    }
}
