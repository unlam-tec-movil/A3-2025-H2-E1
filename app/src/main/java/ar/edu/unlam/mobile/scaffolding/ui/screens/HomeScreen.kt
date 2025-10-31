package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.*
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
    val eventos = listOf(
        Evento("Concierto de Rock", -34.5508002, -58.4548101),
        Evento("Feria de Libro", -34.641347, -58.561187)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        //  Mapa de fondo con los 2 eventos
        NearbyMap(
            nearbyEvents = eventos,
            modifier = Modifier.matchParentSize(),
            onEventoClick = { eventoSeleccionado = it }
        )

        //  Contenido encima del mapa (barra de búsqueda y saludo)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // barra de búsqueda
            EventSearchBar(
                searchUiState = searchBarState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                onSuggestionSelected = { event -> viewModel.onEventSelected(event) },
                onActiveChange = viewModel::onActiveChange
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
                text = { Text("Detalles próximamente...") }
            )
        }
    }
}
