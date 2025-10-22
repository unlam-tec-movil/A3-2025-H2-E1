package ar.edu.unlam.mobile.scaffolding.ui.components

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    location: String,
    date: Date,
    coordinates: LatLng,
    myLocation: LatLng,
    isDistanceFilter: Boolean,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // imagen
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del evento",
                modifier =
                    modifier
                        .height(180.dp)
                        .fillMaxWidth(),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(6.dp))

            // fechas
            val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
            val dateString = dateFormat.format(date)
            Text(
                text = "Fecha: $dateString",
                style = MaterialTheme.typography.bodyMedium,
            )

            val timeFormat = SimpleDateFormat("HH:mm 'hs'", Locale.getDefault())
            val timeString = timeFormat.format(date)
            Text(
                text = "Horario: $timeString",
                style = MaterialTheme.typography.bodyMedium,
            )
            // ubicacion
            Text(
                text = "Ubicación: $location",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (isDistanceFilter) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    coordinates.latitude,
                    coordinates.longitude,
                    myLocation.latitude,
                    myLocation.longitude,
                    results,
                )

                Text(
                    text = "Distancia: ${results[0] / 100} Km",
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                val remaining = relativeTimeCustom(date)
                Text(
                    text = remaining,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

fun relativeTimeCustom(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = date.time - now
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days >= 2 -> "En $days días"
        days == 1L -> "Mañana"
        days == -1L -> "Ayer"
        days <= -2 -> "Hace ${-days} días"
        hours in 2..23 -> "En $hours horas"
        minutes in 1..59 -> "En $minutes minutos"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    val calendar =
        Calendar.getInstance().apply {
            set(2025, 11, 20, 20, 15)
        }

    MaterialTheme {
        EventCard(
            imageUrl = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
            title = "Concierto de Rock",
            location = "Estadio River Plate",
            date = calendar.time,
            coordinates = LatLng(-33.603684, -58.381559),
            myLocation = LatLng(-34.603684, -58.381559),
            isDistanceFilter = true,
        )
    }
}
