package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventFilterButton(
    modifier: Modifier = Modifier,
    isDistance: Boolean,
    onClick: (Boolean) -> Unit,
) {
    IconButton(onClick = { onClick(!isDistance) }, modifier = modifier) {
        Icon(
            imageVector = if (isDistance) Icons.Filled.AccessTime else Icons.AutoMirrored.Filled.DirectionsRun,
            contentDescription = "Distancia/Tiempo",
        )
    }
}

@Composable
@Preview
fun EventFilterButtonPreview() {
    var isDistance = false
    EventFilterButton(isDistance = isDistance, onClick = { newValue -> isDistance = newValue })
}
