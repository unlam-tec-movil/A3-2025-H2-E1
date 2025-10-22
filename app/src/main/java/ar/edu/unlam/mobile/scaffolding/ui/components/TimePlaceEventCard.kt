package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimePlaceEventCard(
    modifier: Modifier = Modifier,
    event: Event,
) {
    val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
    val dateString = dateFormat.format(event.dateTime)
    val timeFormat = SimpleDateFormat("HH:mm 'hs'", Locale.getDefault())
    val timeString = timeFormat.format(event.dateTime)

    Column(
        modifier = modifier,
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Lugar del evento",
            )
            Text(
                modifier =
                    modifier
                        .padding(4.dp),
                text = event.description,
            )
        }

        Row {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Dia y hora del evento",
            )
            Text(
                modifier =
                    modifier
                        .padding(4.dp),
                text = "$dateString - $timeString",
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TimePlaceEventCardPreview() {
    TimePlaceEventCard(
        event =
            Event(
                id = "1",
                title = "Evento de prueba",
                description = "Lugar de prueba",
                dateTime = Date(),
                lat = -34.6037,
                lng = -58.3816,
                image = null,
                beforeImage = listOf(),
                afterImage = null,
                membersId = null,
                creatorId = 1,
                saved = false,
                participating = false,
            ),
    )
}
