package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NameCompositionDialog(
    onNameConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String,
    existingName: String? = null,
) {
    val focusRequester = FocusRequester()
    Dialog(onDismissRequest = onDismiss) {
        val text = remember { mutableStateOf(existingName ?: "") }
        Card {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(20.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.subtitle1,
                )
                OutlinedTextField(
                    value = text.value,
                    onValueChange = { text.value = it },
                    maxLines = 1,
                    label = {
                        Text(text = "Composition Name")
                    },
                    modifier = Modifier.focusRequester(focusRequester)
                )

                // Focus the text field when it first appears
                LaunchedEffect(key1 = true) {
                    focusRequester.requestFocus()
                }

                TextButton(
                    onClick = { onNameConfirmed(text.value) },
                    enabled = text.value.isNotEmpty()
                ) {
                    Text(text = "OK")
                }
            }
        }
    }
}
