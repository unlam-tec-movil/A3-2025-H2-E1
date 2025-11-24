package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.ui.theme.ScaffoldingV2Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmParticipationComponent(
    eventName: String = "sin informacion",
    eventDate: String = "sin informacion",
    eventPlace: String = "sin informacion",
    onBackClick: () -> Unit = {},
    onAddToCalendarClick: () -> Unit = {},
    onParticipateClick: () -> Unit = {},
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TopAppBar(
                title = { Text("Confirmar participación") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Confirmación",
                tint = MaterialTheme.colorScheme.secondary,
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
                color = Color.Black,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Fecha",
                            tint = Color(0xFF616161),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = eventDate, color = Color.Black)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Lugar",
                            tint = Color(0xFF616161),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = eventPlace, color = Color.Black)
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onAddToCalendarClick,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50),
                        ),
                ) {
                    Text("Agregar al calendario")
                }

                Button(
                    onClick = onParticipateClick,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White,
                        ),
                ) {
                    Text("Participar")
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ConfirmParticipationComponentPreview() {
    ScaffoldingV2Theme {
        ConfirmParticipationComponent(
            eventName = "Jornada de Limpieza en la Playa",
            eventDate = "Domingo 27 de Octubre, 10:00 hs",
            eventPlace = "Playa Grande, Mar del Plata",
            onBackClick = {},
            onAddToCalendarClick = {},
            onParticipateClick = {},
        )
    }
}
