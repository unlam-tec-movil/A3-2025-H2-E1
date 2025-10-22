package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.model.EventListEntity
import ar.edu.unlam.mobile.scaffolding.ui.components.EventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventFilterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import com.google.android.gms.maps.model.LatLng
import java.util.Date

@Composable
fun EventListScreen(modifier: Modifier = Modifier) {
    // TODO: crear viewmodel y obtener eventos desde ahi
    val isDistance = remember { mutableStateOf(false) }
    val events = sampleEvents
    // TODO: obtener mis coordenadas y enviarlas a cada EventCard

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                title = "Próximos eventos",
                actions = {
                    EventFilterButton(
                        isDistance = isDistance,
                        onClick = {
                            isDistance.value = it
                            // TODO: ordenar events segun el criterio
                        },
                    )
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(events) { event ->
                EventCard(
                    imageUrl = event.image,
                    title = event.title,
                    location = event.description,
                    date = event.dateTime,
                    coordinates = LatLng(event.lat, event.lng),
                    isDistanceFilter = isDistance.value,
                    myLocation = LatLng(-33.603684, -58.381559), // TODO obtener mi ubicacion real
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

private val sampleEvents =
    listOf(
        EventListEntity(
            id = "1",
            image = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
            title = "Concierto de Rock",
            description = "Teatro Central",
            dateTime = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24), // 1 día atrás
            lat = -33.603684,
            lng = -58.381559,
        ),
        EventListEntity(
            id = "2",
            image = "https://shorturl.at/QUHmG",
            title = "Feria de Libro",
            description = "Plaza Mayor",
            dateTime = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2), // en 2 días
            lat = -33.603684,
            lng = -58.381559,
        ),
        EventListEntity(
            id = "3",
            image = "https://shorturl.at/ZehlK",
            title = "Charla de Tecnología",
            description = "Auditorio 1",
            dateTime = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 5), // en 5 horas
            lat = -33.603684,
            lng = -58.381559,
        ),
    )

@Preview(showBackground = true)
@Composable
fun PreviewEventListScreen() {
    EventListScreen()
}
