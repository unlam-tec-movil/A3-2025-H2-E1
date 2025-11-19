package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.AnimatedEventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.Event
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
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

    // cambiar por un EventList despues, y que sea un valor del uiState de paso
    var eventoSeleccionado by remember { mutableStateOf<SuggestedEvent?>(null) }

    var showCreateEventDialog by remember { mutableStateOf(false) }

    // este seguramente tambien terminen en uiState
    var isSessionActive by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (val helloState = uiState.helloMessageState) {
            MessageUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is MessageUIState.Success -> {
                NearbyMap(
                    nearbyEvents = uiState.eventList,
                    modifier = Modifier.matchParentSize(),
                    lat = null,
                    lng = null,
                    onEventoClick = { eventoSeleccionado = it },
                )

                //  Contenido encima del mapa (barra de búsqueda y saludo)
                // TODO llamar al "eventList" con la id del "suggestedEvent" del repositorio
                // De momento solo muestra los datos de suggestedEvent
                eventoSeleccionado?.let { evento ->
                    val eventCard =
                        Event(
                            id = evento.id,
                            name = evento.title,
                            dateTime = "01/12/2025 - 20:00 hs",
                            image1 = "https://picsum.photos/300/200",
                            image2 = "https://picsum.photos/301/200",
                            creatorId = 1,
                            lat = evento.lat,
                            lng = evento.lng,
                        )

                    AnimatedEventCard(eventCard = eventCard, onClose = { eventoSeleccionado = null })
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(zIndex = 20f),
                ) {
                    // barra de búsqueda
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearch = viewModel::onSearch,
                        onSuggestionSelected = { event ->
                            viewModel.onEventSelected(event)
                            eventoSeleccionado = event
                        },
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
                                text =
                                    if (uiState.eventList.size > 1) {
                                        "Resultados de búsqueda: ${uiState.eventList.size}"
                                    } else {
                                        "Resultado de búsqueda"
                                    },
                                modifier = Modifier.padding(horizontal = 6.dp),
                            )
                        }
                    }
                }

                Column(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
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
                            .padding(16.dp)
                            .align(Alignment.Center),
                )
            }
        }
    }
}
