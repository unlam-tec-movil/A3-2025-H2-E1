package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.ui.theme.PrimaryGreen
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventHomeCard(
    event: EventList,
    distance: String,
    onViewEventClick: () -> Unit,
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Título
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            // Fecha formateada
            val formattedDate =
                remember(event.dateTime) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                    sdf.format(Date(event.dateTime))
                }
            Text(text = "Fecha: $formattedDate", style = MaterialTheme.typography.bodyMedium)

            // Descripción
            if (!event.description.isNullOrEmpty()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            // Imagen
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

            // Distancia
            Text(text = "Distancia: $distance", style = MaterialTheme.typography.bodySmall)

            // Botón
            Button(
                onClick = onViewEventClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Ver Evento", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

fun getEventDateById(
    eventList: List<EventList>,
    eventId: String,
): Long? = eventList.find { it.id == eventId }?.dateTime
