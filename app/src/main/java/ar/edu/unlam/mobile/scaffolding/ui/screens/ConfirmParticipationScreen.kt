package ar.edu.unlam.mobile.scaffolding.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmParticipationScreen(
    eventName: String = "Jornada de Limpieza en la Playa",
    eventDate: String = "Domingo 27 de Octubre, 10:00 hs",
    eventPlace: String = "Playa Grande, Mar del Plata",
    onBackClick: () -> Unit = {},
    onAddToCalendarClick: () -> Unit = {},
    onParticipateClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar participación") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // ✅ Ícono de confirmación
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Confirmación",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(96.dp)
            )

            // Texto descriptivo
            Text(
                text = "Compruebe los detalles del evento para confirmar su participación. " +
                        "Apreciamos su esfuerzo para mantener limpia nuestra comunidad.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Text(
                text = eventName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )

            // 📅 Card con fecha y lugar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Fecha",
                            tint = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = eventDate, color = Color.Black)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Lugar",
                            tint = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = eventPlace, color = Color.Black)
                    }
                }
            }

            // Botones
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onAddToCalendarClick,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Agregar al calendario")
                }

                Button(
                    onClick = onParticipateClick,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Participar")
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ConfirmParticipationScreenPreview() {
    ConfirmParticipationScreen(
        eventName = "Jornada de Limpieza en la Playa",
        eventDate = "Domingo 27 de Octubre, 10:00 hs",
        eventPlace = "Playa Grande, Mar del Plata",
        onBackClick = {},
        onAddToCalendarClick = {},
        onParticipateClick = {}
    )
}