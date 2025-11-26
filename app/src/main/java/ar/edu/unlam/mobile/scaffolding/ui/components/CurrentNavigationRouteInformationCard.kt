package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CurrentNavigationRouteInformationCard(
    duration: Long,
    distance: Double,
    onClosesClick: () -> Unit = {},
) {
    val totalMinutes = duration / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val elapsedTime =
        if (hours > 0) {
            "${hours}hr $minutes"
        } else {
            "$minutes minutos"
        }

    val distanceKm = String.format("%.1f", distance / 1000)

    OutlinedCard(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = Color.White.copy(alpha = 0.75f),
                contentColor = Color.Black,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Duración",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black.copy(alpha = 0.7f),
                )
                Text(
                    elapsedTime,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Distancia",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black.copy(alpha = 0.7f),
                )
                Text(
                    "$distanceKm km",
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                )
            }
            Column(
                verticalArrangement = Arrangement.Top,
            ) {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = onClosesClick,
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCurrentNavigationRouteInformationCard() {
    CurrentNavigationRouteInformationCard(1200000, 526.0)
}
