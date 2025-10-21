package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    isExpanded: Boolean,
    onActiveChange: (Boolean) -> Unit,
) {
    val transition = updateTransition(isExpanded)
    val paddingSize by transition.animateDp(label = "padding") { state ->
        if (state) 0.dp else 12.dp
    }
    val paddingInputFieldSize by transition.animateDp(label = "padding") { state ->
        if (state) 8.dp else 0.dp
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = {
                    onSearch(it)
                    onActiveChange(false)
                },
                expanded = isExpanded,
                onExpandedChange = onActiveChange,
                placeholder = { Text("Buscar") },
                leadingIcon = {
                    if (isExpanded) {
                        IconButton(onClick = { onActiveChange(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cerrar búsqueda",
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar eventos",
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSearchQueryChange("")
                                onActiveChange(false)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda",
                            )
                        }
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = paddingInputFieldSize),
            )
        },
        expanded = isExpanded,
        onExpandedChange = onActiveChange,
        shape = SearchBarDefaults.inputFieldShape,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        colors =
            SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                dividerColor = Color.Transparent,
                inputFieldColors =
                    inputFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingSize),
    ) {
        // TODO: Contenido de la barra de búsqueda
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "No hay resultados",
                modifier =
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
            )
        }
    }
}

@Preview
@Composable
fun EventSearchBarPreview() {
    ScaffoldingV2Theme {
        EventSearchBar(
            searchQuery = "",
            onSearchQueryChange = {},
            onSearch = {},
            isExpanded = false,
            onActiveChange = {},
        )
    }
}

@Preview
@Composable
fun EventSearchBarExpandedPreview() {
    ScaffoldingV2Theme {
        EventSearchBar(
            searchQuery = "buscar evento",
            onSearchQueryChange = {},
            onSearch = {},
            isExpanded = true,
            onActiveChange = {},
        )
    }
}
