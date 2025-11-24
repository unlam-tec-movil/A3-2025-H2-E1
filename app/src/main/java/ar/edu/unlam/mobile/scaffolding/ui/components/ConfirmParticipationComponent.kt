package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmParticipationComponent(
    event: Event,
    eventName: String = "sin informacion",
    onBackClick: () -> Unit = {},
    onAddToCalendarClick: () -> Unit = {},
    onParticipateClick: () -> Unit = {},
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopBar(
                title = "Confirmar participación",
                onNavigateBack = onBackClick,
            )
        },
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Confirmación",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp),
            )

            Text(
                text =
                    "Compruebe los detalles del evento para confirmar su participación. " +
                        "Apreciamos su esfuerzo para mantener limpia nuestra comunidad.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )

            Text(
                text = eventName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                TimePlaceEventCard(
                    event = event,
                    onLocationClick = { _, _ -> },
                    modifier = Modifier.padding(6.dp),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .padding(bottom = 22.dp)
                        .fillMaxWidth(),
            ) {
                SecondaryButton(
                    text = "Agregar al calendario",
                    onClick = onAddToCalendarClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                )
                PrimaryButton(
                    text = "Participar",
                    onClick = onParticipateClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ConfirmParticipationComponentPreview() {
    ScaffoldingV2Theme {
        ConfirmParticipationComponent(
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
                        UserItem(
                            id = 1,
                            name = "Pepe Papa",
                            avatarUrl = null,
                            description = null,
                        ),
                    saved = false,
                    participating = false,
                ),
            eventName = "Jornada de Limpieza en la Playa",
            onBackClick = {},
            onAddToCalendarClick = {},
            onParticipateClick = {},
        )
    }
}
