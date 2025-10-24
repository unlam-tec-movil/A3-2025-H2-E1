package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class User(
    val username: String,
    val userAvatar: String?,
    val description: String
)

@Composable
fun ParticipantInfoPopUp(
    user: User,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000)), // fondo semitransparente
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(modifier = Modifier.padding(20.dp)) {

                    // Botón Cerrar X
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black
                        )
                    }

                    // Botón Reportar
                    IconButton(
                        onClick = onReportClick,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning, // triángulo con exclamación
                            contentDescription = "Reportar usuario",
                            tint = Color(0xFFD32F2F)
                        )
                    }

                    // Contenido principal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {

                        // Avatar con borde y fondo gris
                        if (!user.userAvatar.isNullOrEmpty()) {
                            AsyncImage(
                                model = user.userAvatar,
                                contentDescription = "Avatar de ${user.username}",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray, CircleShape)
                                    .padding(2.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar vacío",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray, CircleShape)
                                    .padding(2.dp),
                                tint = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre de usuario
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Descripción
                        Text(
                            text = user.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParticipantInfoPopUpPreview() {
    // Estado temporal para simular el cierre del diálogo
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        ParticipantInfoPopUp(
            user = User(
                username = "Nombre usuario",
                userAvatar = null, // prueba con null o una URL de imagen
                description = "Este es un ejemplo de descripción de usuario para ver cómo se ve el PopUp en Compose."
            ),
            onDismiss = { showDialog.value = false },
            onReportClick = { /* Acción de reportar */ }
        )
    }
}