package dev.bryanlindsey.musicgenerator3.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Tungsten
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeNavigationScreen(
    onNavigateToGeneration: () -> Unit,
    onNavigateToSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            shape = CircleShape,
            onClick = onNavigateToGeneration,
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Icon(Icons.Default.Tungsten, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Spark")
        }
        Button(
            shape = CircleShape,
            onClick = onNavigateToSave,
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Icon(Icons.Default.QueueMusic, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Saved")
        }
    }
}
