package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.ui.common.EventSearchState
import ar.edu.unlam.mobile.scaffolding.ui.screens.SearchUIState
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
    searchUiState: SearchUIState,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSuggestionSelected: (SuggestedEvent) -> Unit,
    onActiveChange: (Boolean) -> Unit,
) {
    val isExpanded = searchUiState.isExpanded
    val searchQuery = searchUiState.currentQuery
    val searchState = searchUiState.searchState

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
                onSearch = onSearch,
                expanded = isExpanded,
                onExpandedChange = onActiveChange,
                placeholder = {
                    Text(
                        text = "Buscar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                },
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
                                onSearch("")
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
        when (searchState) {
            EventSearchState.Idle -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Buscar eventos",
                        modifier =
                            Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                    )
                }
            }
            EventSearchState.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
            is EventSearchState.Success -> {
                if (searchState.events.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No se encontraron CleanUps con ese nombre",
                            modifier =
                                Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                        )
                    }
                } else {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                    ) {
                        AnimatedVisibility(
                            visible = searchState.events.size > 1 && searchUiState.currentQuery.isNotEmpty(),
                            enter =
                                expandVertically(
                                    animationSpec = tween(durationMillis = 170),
                                ),
                            exit =
                                shrinkVertically(
                                    animationSpec = tween(durationMillis = 170),
                                ),
                        ) {
                            Card(
                                onClick = { onSearch(searchUiState.currentQuery) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text =
                                        buildAnnotatedString {
                                            append("Mostrar resultados para '")
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(searchUiState.currentQuery)
                                            }
                                            append("' en el mapa.")
                                        },
                                    textAlign = TextAlign.Center,
                                    modifier =
                                        Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                )
                            }
                        }
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier =
                                        Modifier
                                            .padding(top = 4.dp)
                                            .padding(horizontal = 2.dp),
                                ) {
                                    Text(
                                        text = if (searchState.events.size > 1) "Resultados" else "Resultado",
                                        color =
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.7f,
                                            ),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                    )
                                    HorizontalDivider(
                                        thickness = 0.8.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
                            }

                            items(searchState.events) { event ->
                                Text(
                                    text = event.title,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable { onSuggestionSelected(event) }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                )
                            }
                        }
                    }
                }
            }
            is EventSearchState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    searchState.message?.let {
                        Text(
                            text = it,
                            modifier =
                                Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EventSearchBarPreview() {
    ScaffoldingV2Theme {
        EventSearchBar(
            searchUiState = SearchUIState(isExpanded = false),
            onSearchQueryChange = {},
            onSearch = {},
            onSuggestionSelected = {},
            onActiveChange = {},
        )
    }
}

@Preview
@Composable
fun EventSearchBarExpandedPreview() {
    ScaffoldingV2Theme {
        EventSearchBar(
            searchUiState =
                SearchUIState(
                    isExpanded = true,
                    currentQuery = "buscar evento",
                    searchState =
                        EventSearchState.Success(
                            "buscar evento",
                            listOf(
                                SuggestedEvent(id = "1", title = "Concierto de Rock", lat = 0.0, lng = 0.0),
                                SuggestedEvent(id = "2", title = "Feria de Libro", lat = 0.0, lng = 0.0),
                            ),
                        ),
                ),
            onSearchQueryChange = {},
            onSearch = {},
            onSuggestionSelected = {},
            onActiveChange = {},
        )
    }
}
