package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventFilterButton(
    modifier: Modifier = Modifier,
    isDistance: MutableState<Boolean>,
    onClick: (Boolean) -> Unit,
) {
    IconButton(onClick = { onClick(!isDistance.value) }, modifier = modifier) {
        Icon(
            imageVector = if (isDistance.value) Icons.AutoMirrored.Filled.DirectionsRun else Icons.Filled.AccessTime,
            contentDescription = "Distancia/Tiempo",
        )
    }
}

@Composable
@Preview
fun EventFilterButtonPreview() {
    val isDistance = remember { mutableStateOf(false) }
    EventFilterButton(isDistance = isDistance, onClick = { newValue -> isDistance.value = newValue })
}