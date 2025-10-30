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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.utils.getAddressFromCoordinates
import java.text.SimpleDateFormat
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
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(event.lat, event.lng) {
        address = getAddressFromCoordinates(context, event.lat, event.lng)
    }

    Column(
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Lugar del evento",
            )
            Text(
                modifier =
                    modifier
                        .padding(4.dp),
                text = address,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
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
                dateTime = System.currentTimeMillis(),
                lat = -34.622681,
                lng = -58.611251,
                image = null,
                beforeImage = listOf(),
                afterImage = null,
                members = emptyList(),
                creator =
                    User(
                        id = 1,
                        name = "Pepe Papa",
                        avatarUrl = null,
                        description = null,
                    ),
                saved = false,
                participating = false,
            ),
    )
}
