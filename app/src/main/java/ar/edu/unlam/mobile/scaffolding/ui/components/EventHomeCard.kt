package ar.edu.unlam.mobile.scaffolding.ui.components

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventHomeCard(
    event: EventItem,
    distance: LatLng,
    onViewEventClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // --- X para cerrar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleLarge,
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .clickable { onCloseClick() },
                )
            }

            // --- Título ---
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            // --- Fecha ---
            val formattedDate =
                remember(event.dateTime) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    sdf.format(Date(event.dateTime))
                }
            Text(text = "Fecha: $formattedDate")

            // --- Descripción ---
            event.description?.takeIf { it.isNotEmpty() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // --- Imagen ---
            AsyncImage(
                model = event.image ?: R.drawable.sin_imagen,
                contentDescription = "Imagen del evento",
                placeholder = rememberAsyncImagePainter(R.drawable.sin_imagen),
                error = rememberAsyncImagePainter(R.drawable.sin_imagen),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )

            // --- Distancia ---
            val results = FloatArray(1)
            Location.distanceBetween(
                event.lat,
                event.lng,
                distance.latitude,
                distance.longitude,
                results,
            )
            val distanceString = String.format(Locale.getDefault(), "%.2f", results[0] / 1000)

            Text(text = "Distancia: $distanceString km")

            // --- Botón Ver Evento ---
            Button(
                onClick = onViewEventClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Ver Evento")
            }
        }
    }
}

fun getEventDateById(
    eventItem: List<EventItem>,
    eventId: String,
): Long? = eventItem.find { it.id == eventId }?.dateTime
