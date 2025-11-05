package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.Evento
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingButtons
import ar.edu.unlam.mobile.scaffolding.ui.components.NearbyMap

const val HOME_SCREEN_ROUTE = "home"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchUiState.collectAsStateWithLifecycle()

    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    var showCreateEventDialog by remember { mutableStateOf(false) }

    var isSessionActive by remember { mutableStateOf(false) }

    // TODO Traer una lista de "suggestedEvents" del viewmodel llamando al repositorio
    // Lista fija de eventos de prueba
    val eventos =
        listOf(
            Evento("Concierto de Rock", -34.5508002, -58.4548101),
            Evento("Feria de Libro", -34.641347, -58.561187),
        )

    Box(modifier = Modifier.fillMaxSize()) {
        when (val helloState = uiState.helloMessageState) {
            MessageUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is MessageUIState.Success -> {
                //  Mapa de fondo con los 2 eventos
                NearbyMap(
                    nearbyEvents = eventos,
                    modifier = Modifier.matchParentSize(),
                    onEventoClick = { eventoSeleccionado = it },
                )

                //  Contenido encima del mapa (barra de búsqueda y saludo)
                Column(modifier = Modifier.fillMaxWidth()) {
                    // barra de búsqueda
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearch = viewModel::onSearch,
                        onSuggestionSelected = { event -> viewModel.onEventSelected(event) },
                        onActiveChange = viewModel::onActiveChange,
                    )
                    AnimatedVisibility(searchBarState.lastQuery.isNotEmpty()) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        ) {
                            Text(
                                text = "Resultado de búsqueda",
                                modifier = Modifier.padding(horizontal = 6.dp),
                            )
                        }
                    }
                }

                // Primero traer una lista de "suggestedEvents" del repositorio. TODO anterior
                // TODO llamar al eventList e implementar C3 EventHomeCard
                // Diálogo al tocar un evento de prueba
                eventoSeleccionado?.let { evento ->
                    AlertDialog(
                        onDismissRequest = { eventoSeleccionado = null },
                        confirmButton = {
                            Button(onClick = { eventoSeleccionado = null }) {
                                Text("Cerrar")
                            }
                        },
                        title = { Text(evento.nombre) },
                        text = { Text("Detalles próximamente...") },
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp, bottom = 85.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    FloatingButtons(
                        isSessionActive = isSessionActive,
                        onClickCamera = { },
                        onClickAddEvent = { showCreateEventDialog = true },
                        onClickStartSession = {
                            isSessionActive = !isSessionActive
                        },
                    )
                }
                if (showCreateEventDialog) {
                    CreateEventPopUp(
                        onDismiss = { showCreateEventDialog = false },
                        onConfirm = { name, location, dateTime, imageUri ->
                            viewModel.createEvent(name, location, dateTime, imageUri)
                            showCreateEventDialog = false
                        },
                    )
                }
            }

            is MessageUIState.Error -> {
                Text(
                    text = helloState.message,
                    modifier =
                        Modifier
                            .padding(96.dp)
                            .align(Alignment.Center),
                )
            }
        }
    }
}
