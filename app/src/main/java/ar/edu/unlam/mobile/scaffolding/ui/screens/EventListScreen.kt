package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.EventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventFilterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import com.google.android.gms.maps.model.LatLng

@Composable
fun EventListScreen(
    modifier: Modifier = Modifier,
    viewModel: EventListViewModel = hiltViewModel(),
    navController: NavController? = null,
) {
    val state by viewModel.uiState.collectAsState()

    // TODO obtener mis coordenadas y enviarlas a cada EventCard

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                title = "Próximos eventos",
                actions = {
                    EventFilterButton(
                        isDistance = state.isDistance,
                        onClick = {
                            viewModel.updateFilter(it)
                        },
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { innerPadding ->
        when (state.currentState) {
            is MessageUIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
            is MessageUIState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(state.events) { eventList ->
                        EventCard(
                            imageUrl = eventList.image,
                            title = eventList.title,
                            date = eventList.dateTime,
                            coordinates = LatLng(eventList.lat, eventList.lng),
                            isDistanceFilter = state.isDistance,
                            // TODO obtener mi ubicacion real
                            myLocation = LatLng(-33.603684, -58.381559),
                            modifier =
                                Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        navController?.navigate("eventDetails/${eventList.id}")
                                    },
                        )
                    }
                }
            }
            is MessageUIState.Error -> {
                // TODO mostrar error
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventListScreen() {
    ScaffoldingV2Theme {
        EventListScreen()
    }
}
