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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.*
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

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔹 Mapa de fondo
        NearbyMap(
            nearbyEvents = searchBarState.eventList.map { e ->
                Evento(e.title, e.lat, e.lng)
            },
            modifier = Modifier.matchParentSize(),
            onEventoClick = { evento ->
                println("Evento seleccionado: ${evento.nombre}")
            }
        )

        // 🔹 Contenido encima del mapa
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            EventSearchBar(
                searchUiState = searchBarState,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearch = viewModel::onSearch,
                onSuggestionSelected = { event ->
                    viewModel.onEventSelected(event)
                },
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
    }
}