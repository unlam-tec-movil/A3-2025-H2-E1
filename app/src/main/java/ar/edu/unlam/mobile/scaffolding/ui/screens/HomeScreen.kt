package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.Evento
import ar.edu.unlam.mobile.scaffolding.ui.components.Greeting
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

    // Lista fija de eventos de prueba
    val eventos =
        listOf(
            Evento("Concierto de Rock", -34.5508002, -58.4548101),
            Evento("Feria de Libro", -34.641347, -58.561187),
        )

    Box(modifier = Modifier.fillMaxSize()) {
        //  Mapa de fondo con los 2 eventos
        NearbyMap(
            nearbyEvents = eventos,
            modifier = Modifier.matchParentSize(),
            onEventoClick = { eventoSeleccionado = it },
        )

        //  Contenido encima del mapa (barra de búsqueda y saludo)
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            // barra de búsqueda
            EventSearchBar(
                searchUiState = searchBarState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                onSuggestionSelected = { event -> viewModel.onEventSelected(event) },
                onActiveChange = viewModel::onActiveChange,
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val helloState = uiState.helloMessageState) {
                is MessageUIState.Success -> Greeting(helloState.message, modifier)
                is MessageUIState.Error -> Text("Error: ${helloState.message}")
                else -> CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(searchBarState.currentQuery)
        }

        // Diálogo al tocar un evento
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
    }
}
