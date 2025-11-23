package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun FloatingButtons(
    onClickAddEvent: () -> Unit,
    onClickCenterMap: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End,
    ) {
        FloatingActionButton(onClickAddEvent) {
            Icon(Icons.Default.Add, "Agregar evento")
        }
        FloatingActionButton(onClickCenterMap) {
            Icon(Icons.Default.CenterFocusWeak, "Centrar mapa")
        }
    }
}
