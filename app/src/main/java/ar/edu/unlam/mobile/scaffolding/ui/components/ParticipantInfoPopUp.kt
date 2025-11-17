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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import coil.compose.AsyncImage

@Composable
fun ParticipantInfoPopUp(
    user: User,
    onDismiss: () -> Unit,
    // recibe el comentario
    onReportClick: (String) -> Unit,
) {
    val showReportDialog = remember { mutableStateOf(false) }
    val reportComment = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
            // fondo semitransparente
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            ) {
                Box(modifier = Modifier.padding(20.dp)) {
                    // Botón Cerrar X
                    IconButton(
                        onClick = onDismiss,
                        modifier =
                            Modifier
                                .align(Alignment.TopStart)
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black,
                        )
                    }

                    // Botón Reportar
                    IconButton(
                        onClick = { showReportDialog.value = true },
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = CircleShape),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Reportar usuario",
                            tint = Color(0xFFD32F2F),
                        )
                    }

                    // Contenido principal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                    ) {
                        val avatar = user.avatarUrl
                        if (!avatar.isNullOrEmpty()) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "Avatar de ${user.name}",
                                modifier =
                                    Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray, CircleShape)
                                        .padding(2.dp),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar vacío",
                                modifier =
                                    Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray, CircleShape)
                                        .padding(2.dp),
                                tint = Color.Gray,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = user.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }

            // Popup de reporte
            if (showReportDialog.value) {
                Dialog(onDismissRequest = { showReportDialog.value = false }) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Reportar a ${user.name}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reportComment.value,
                                onValueChange = { reportComment.value = it },
                                label = { Text("Comentario") },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.align(Alignment.End),
                            ) {
                                TextButton(onClick = { showReportDialog.value = false }) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        onReportClick(reportComment.value)
                                        showReportDialog.value = false
                                        onDismiss()
                                    },
                                ) {
                                    Text("Confirmar")
                                }
                            }
                        }
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

    val sampleUser =
        User(
            id = 1,
            name = "Nombre usuario",
            avatarUrl = null,
            description = "Este es un ejemplo de descripción de usuario para ver cómo se ve el PopUp en Compose.",
        )

    if (showDialog.value) {
        ParticipantInfoPopUp(
            user = sampleUser,
            onDismiss = { showDialog.value = false },
            onReportClick = { /* Acción de reportar */ },
        )
    }
}
