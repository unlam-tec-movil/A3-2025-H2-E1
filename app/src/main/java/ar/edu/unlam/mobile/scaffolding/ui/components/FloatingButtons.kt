package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun FloatingButtons(
    onClickAddEvent: () -> Unit,
    onClickCenterMap: () -> Unit,
    isSelectingLocation: Boolean,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End,
    ) {
        FloatingActionButton(
            containerColor =
                if (isSelectingLocation) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
            onClick = onClickAddEvent,
        ) {
            if (isSelectingLocation) {
                Icon(Icons.Default.Check, "Confirmar ubicación")
            } else {
                Icon(Icons.Default.Add, "Agregar evento")
            }
        }
        FloatingActionButton(onClickCenterMap) {
            Icon(Icons.Default.MyLocation, "Centrar mapa")
        }
    }
}
