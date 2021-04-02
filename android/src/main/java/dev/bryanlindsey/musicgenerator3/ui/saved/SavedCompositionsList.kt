package dev.bryanlindsey.musicgenerator3.ui.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.bryanlindsey.musicgenerator3.SavedComposition
import dev.bryanlindsey.musicgenerator3.ui.common.CompositionProperties
import dev.bryanlindsey.musicgenerator3.ui.common.NameCompositionDialog

@Composable
fun SavedCompositionsList(
    savedItemsList: List<SavedComposition>,
    onExportClicked: (SavedComposition) -> Unit,
    onItemClicked: (SavedComposition) -> Unit,
    onPlayClicked: (SavedComposition) -> Unit,
    onDeleteItemClicked: (SavedComposition) -> Unit,
    onItemRenamed: (SavedComposition) -> Unit,
) {
    val itemToDeleteState: MutableState<SavedComposition?> = remember { mutableStateOf(null) }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(savedItemsList, key = { it.id }) {
            SavedCompositionItem(
                composition = it,
                onPlayClicked = onPlayClicked,
                onDeleteClicked = { itemToDeleteState.value = it },
                onExportClicked = onExportClicked,
                onItemClicked = onItemClicked,
                onRenameConfirmed = onItemRenamed,
            )
        }
    }
    val itemToDelete = itemToDeleteState.value
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDeleteState.value = null },
            title = { Text(text = "Delete") },
            text = { Text(text = "Are you sure you want to delete this composition?") },
            confirmButton = {
                Button(onClick = {
                    onDeleteItemClicked(itemToDelete)
                    itemToDeleteState.value = null
                }) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(onClick = { itemToDeleteState.value = null }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
fun SavedCompositionItem(
    composition: SavedComposition,
    onPlayClicked: (SavedComposition) -> Unit,
    onDeleteClicked: (SavedComposition) -> Unit,
    onExportClicked: (SavedComposition) -> Unit,
    onItemClicked: (SavedComposition) -> Unit,
    onRenameConfirmed: (SavedComposition) -> Unit,
) {

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .clickable {
                onItemClicked(composition)
            }
            .then(Modifier.widthIn(max = 500.dp))
    ) {

        val isRenameDialogOpen = remember { mutableStateOf(false) }
        if (isRenameDialogOpen.value) {
            NameCompositionDialog(
                onNameConfirmed = {
                    val updatedComposition = composition.copy(name = it)
                    onRenameConfirmed(updatedComposition)
                    isRenameDialogOpen.value = false
                },
                onDismiss = { isRenameDialogOpen.value = false },
                title = "Rename",
                existingName = composition.name
            )
        }

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                CompositionProperties(
                    composition = composition.composition,
                    name = composition.name,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // TODO: Add rename button

            IconButton(onClick = { onPlayClicked(composition) }) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
            }
            val isOpen = remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { isOpen.value = !isOpen.value }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = isOpen.value,
                    onDismissRequest = { isOpen.value = false }
                ) {
                    DropdownMenuItem(onClick = {
                        isOpen.value = false
                        onExportClicked(composition)
                    }) {
                        Text("Export as MIDI")
                    }
                    DropdownMenuItem(onClick = {
                        isRenameDialogOpen.value = true
                        isOpen.value = false
                    }) {
                        Text(text = "Rename")
                    }
                    DropdownMenuItem(onClick = {
                        isOpen.value = false
                        onDeleteClicked(composition)
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
