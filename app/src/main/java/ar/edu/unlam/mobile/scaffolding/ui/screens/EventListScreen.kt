package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.components.EventCard
import java.util.Date
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ar.edu.unlam.mobile.scaffolding.data.datasources.model.Event
import ar.edu.unlam.mobile.scaffolding.ui.components.EventFilterButton
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar

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
                    imageUrl = event.imageUrl,
                    title = event.title,
                    location = event.location,
                    date = event.date,
                    coordinates = event.coordinates,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

private val sampleEvents =
    listOf(
        Event(
            imageUrl = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
            title = "Concierto de Rock",
            location = "Teatro Central",
            date = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24) // 1 día atrás
        ),
        Event(
            imageUrl = @Suppress("ktlint:standard:max-line-length")
            "https://images.unsplash.com/photo-1631888717179-7e213fc93e1d?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1176",
            title = "Feria de Libro",
            location = "Plaza Mayor",
            date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2) // en 2 días
        ),
        Event(
            imageUrl = @Suppress("ktlint:standard:max-line-length")
            "https://images.unsplash.com/photo-1592383010275-b028451b2947?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1171",
            title = "Charla de Tecnología",
            location = "Auditorio 1",
            date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 5), // en 5 horas
        ),
    )

@Preview(showBackground = true)
@Composable
fun PreviewEventListScreen() {
    EventListScreen()
}