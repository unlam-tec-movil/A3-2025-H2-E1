package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme
import ar.edu.unlam.mobile.scaffolding.utils.getAddressFromCoordinates
import org.osmdroid.util.GeoPoint

@Composable
fun SelectEventPositionBar(
    modifier: Modifier = Modifier,
    currentLocation: GeoPoint?,
) {
    val context = LocalContext.current
    var locationString by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = currentLocation) {
        locationString =
            if (currentLocation != null) {
                getAddressFromCoordinates(context, currentLocation.latitude, currentLocation.longitude)
            } else {
                null
            }
    }

    val isSuccess = locationString != null && locationString != "Error obteniendo dirección"
    val transition = updateTransition(isSuccess)
    val icon = if (isSuccess) Icons.Default.Check else Icons.Default.LocationOn
    val iconColor = if (isSuccess) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.primary

    val containerColor by transition.animateColor(
        label = "containerColor",
        transitionSpec = { tween(300) },
    ) { state ->
        if (state) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
    }

    Card(
        shape = RoundedCornerShape(50.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Estado de la ubicación",
                tint = iconColor,
            )

            Text(
                text = locationString ?: "Toca el mapa para seleccionar",
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        color =
                            if (isSuccess) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        fontWeight = FontWeight.Bold,
                    ),
            )
        }
    }
}

@Preview
@Composable
fun SelectEventPositionBarPreview() {
    ScaffoldingV2Theme {
        SelectEventPositionBar(
            currentLocation = GeoPoint(-34.603738, -58.34),
        )
    }
}
