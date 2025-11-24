package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventFilterButton(
    modifier: Modifier = Modifier,
    onDistanceFilter: () -> Unit,
    onDateFilter: () -> Unit,
) {
    var selectedOption: Int by remember { mutableStateOf(0) }

    val options: List<EventFilterOption> =
        listOf(
            EventFilterOption(
                image = Icons.Filled.AccessTime,
                title = "Más recientes",
                onSelect = {
                    onDateFilter()
                    selectedOption = 0
                },
            ),
            EventFilterOption(
                image = Icons.AutoMirrored.Filled.DirectionsRun,
                title = "Más cercanos",
                onSelect = {
                    onDistanceFilter()
                    selectedOption = 1
                },
            ),
        )

    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Ordenar eventos",
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option.title) },
                    onClick = option.onSelect,
                    leadingIcon = {
                        Icon(
                            imageVector = option.image,
                            contentDescription = option.title,
                            tint =
                                if (selectedOption == index) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        )
                    },
                )
            }
        }
    }
}

class EventFilterOption(
    val image: ImageVector,
    val title: String,
    val onSelect: () -> Unit,
)

@Composable
@Preview
fun EventFilterButtonPreview() {
    var isDistance = false
    EventFilterButton(onDistanceFilter = { isDistance = true }, onDateFilter = { isDistance = false })
}
