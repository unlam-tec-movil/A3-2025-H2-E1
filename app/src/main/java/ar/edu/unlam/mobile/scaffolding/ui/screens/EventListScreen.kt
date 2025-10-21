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
import ar.edu.unlam.mobile.scaffolding.data.datasources.model.Event

@Composable
fun EventListScreen(events: List<Event> = sampleEvents) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp)
    ) {
        items(events) { event ->
            EventCard(
                imageUrl = event.imageUrl,
                title = event.title,
                location = event.location,
                date = event.date,
                locationDistance = event.locationDistance,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

private val sampleEvents = listOf(
    Event(
        imageUrl = "https://picsum.photos/300/200?1",
        title = "Concierto de Rock",
        location = "Teatro Central",
        date = Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24) // 1 día atrás
    ),
    Event(
        imageUrl = "https://picsum.photos/300/200?2",
        title = "Feria de Libro",
        location = "Plaza Mayor",
        date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2) // en 2 días
    ),
    Event(
        imageUrl = "https://picsum.photos/300/200?3",
        title = "Charla de Tecnología",
        location = "Auditorio 1",
        date = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 5) // en 5 horas
    )
)

@Preview(showBackground = true)
@Composable
fun PreviewEventListScreen() {
    EventListScreen()
}