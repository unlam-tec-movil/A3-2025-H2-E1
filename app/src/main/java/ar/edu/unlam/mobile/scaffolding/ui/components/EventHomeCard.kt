package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.ui.theme.PrimaryGreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter

data class Event(
    val id: String,
    val name: String,
    val dateTime: String,
    val image1: String,
    val image2: String,
    val creatorId: Int,
    val lat: Double,
    val lng: Double,
)

@Composable
fun EventHomeCard(
    event: Event,
    distance: String,
    onViewEventClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Nombre del evento
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            // Fecha
            Text(
                text = "Fecha: ${event.dateTime}",
                style = MaterialTheme.typography.bodyMedium,
            )

            // Estado del lugar
            Text(
                text = "Estado del lugar",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            // Fila de imágenes
            EventImagesRow(image1 = event.image1, image2 = event.image2)

            // Distancia
            Text(
                text = "Distancia: $distance",
                style = MaterialTheme.typography.bodySmall,
            )

            // Botón "Ver Evento"
            Button(
                onClick = onViewEventClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Ver Evento", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun EventImagesRow(
    image1: String,
    image2: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = image1.ifBlank { R.drawable.sin_imagen },
            contentDescription = "Imagen 1 del evento",
            placeholder = rememberAsyncImagePainter(R.drawable.sin_imagen),
            error = rememberAsyncImagePainter(R.drawable.sin_imagen),
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )

        AsyncImage(
            model = image2.ifBlank { R.drawable.sin_imagen },
            contentDescription = "Imagen 2 del evento",
            placeholder = rememberAsyncImagePainter(R.drawable.sin_imagen),
            error = rememberAsyncImagePainter(R.drawable.sin_imagen),
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SingleEventPreviewPreview() {
    ScaffoldingV2Theme {
        SingleEventPreview()
    }
}

// prueba simple de EventHomeCard
@Composable
fun SingleEventPreview() {
    val sampleEvent =
        Event(
            id = "1",
            name = "Concierto al Aire Libre",
            dateTime = "01/01/25 - 01:15pm",
            image1 = "", // vacío para probar "Sin imagen"
            image2 = "https://picsum.photos/301/200",
            creatorId = 123,
            lat = -34.6037,
            lng = -58.3816,
        )

    EventHomeCard(
        event = sampleEvent,
        distance = "200 mts",
        onViewEventClick = { /* Acción al tocar el botón */ },
    )
}
// necesita en rawable un archivo fail_even_picture.jpg
// en caso de que no pueda cargar la imagen
