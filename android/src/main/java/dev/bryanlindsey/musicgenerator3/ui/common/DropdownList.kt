package dev.bryanlindsey.musicgenerator3.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DefaultDropdownWidth = 128.dp

@Composable
fun <T> DropdownList(
    listItems: List<T>,
    onItemSelected: (T) -> Unit,
    selectedItem: T,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    getDisplayString: (T) -> String = { it.toString() },
    dropdownHeaderText: String? = null,
) {
    val isOpen = remember { mutableStateOf(false) }
    Column(
        modifier = modifier.then(
            Modifier.width(DefaultDropdownWidth)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { isOpen.value = !isOpen.value }
                .padding(4.dp)
        ) {
            Text(
                text = getDisplayString(selectedItem),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = isOpen.value,
            onDismissRequest = { isOpen.value = false }
        ) {
            if (dropdownHeaderText != null) {
                Text(
                    text = dropdownHeaderText,
                    modifier = Modifier.padding(8.dp),
                )
                Divider()
            }
            listItems.forEach { string ->
                DropdownMenuItem(onClick = {
                    isOpen.value = false
                    onItemSelected(string)
                }) {
                    Text(text = getDisplayString(string))
                }
            }
        }
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.4f))
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}

//@Preview
//@Composable
//fun DropdownListPreview() {
//    MaterialTheme {
//        DropdownList(
//            listItems = listOf(1, 2, 3, 4),
//            onItemSelected = { /*TODO*/ },
//            selectedItem = 1
//        )
//    }
//}
