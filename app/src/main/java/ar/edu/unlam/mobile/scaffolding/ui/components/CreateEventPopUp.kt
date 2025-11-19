package ar.edu.unlam.mobile.scaffolding.ui.components

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import ar.edu.unlam.mobile.scaffolding.ui.theme.Black
import ar.edu.unlam.mobile.scaffolding.ui.theme.White
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateEventPopUp(
    onDismiss: () -> Unit,
    onConfirm: (String, String, LocalDateTime, List<Uri>) -> Unit,
) {
    // Estados del evento
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedImagesUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Estados diálogos
    val openDateDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }

    // Estados componente
    val imagesScrollState = rememberLazyListState()
    var showSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Permisos
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Archivo temporal para foto de la cámara
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    // Launchers
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectedImagesUri = selectedImagesUri + uris
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
        ) { success ->
            if (success) {
                cameraImageUri.value?.let { uri ->
                    selectedImagesUri = selectedImagesUri + uri
                }
            }
        }

    // Diálogo para seleccionar fecha
    if (openDateDialog.value) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled =
            remember {
                derivedStateOf { datePickerState.selectedDateMillis != null }
            }

        DatePickerDialog(
            onDismissRequest = {
                openDateDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        date = datePickerState.getSelectedDate()
                        openDateDialog.value = false
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openDateDialog.value = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }

    // Diálogo para seleccionar hora
    if (openTimeDialog.value) {
        val timePickerState = rememberTimePickerState()
        val confirmEnabled =
            remember {
                derivedStateOf { timePickerState.hour != 0 }
            }

        TimePickerDialog(
            onDismissRequest = {
                openTimeDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        openTimeDialog.value = false
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { openTimeDialog.value = false }) { Text("Cancelar") }
            },
            title = { Text("Hora") },
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }

    // Observador del permiso de la cámara
    LaunchedEffect(cameraPermission.status.isGranted) {
        // Cuando el usuario brinda el permiso para utilizar la cámara ésta se abre
        if (cameraPermission.status.isGranted) {
            val file = createImageFile(context)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            cameraImageUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Título
                Text(
                    text = "Crear nuevo evento",
                    style = MaterialTheme.typography.headlineSmall,
                )

                // Nombre del evento
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del evento") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Ubicación del evento
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación del evento") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Seleccionar fecha del evento
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    onValueChange = {},
                    label = { Text("Fecha del evento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { openDateDialog.value = true },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.secondary,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "seleccionar fecha",
                            )
                        }
                    },
                )

                // Seleccionar hora del evento
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "$hour:$minute hr",
                    onValueChange = {},
                    label = { Text("Horario del evento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(
                            onClick = { openTimeDialog.value = true },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.secondary,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "seleccionar horario",
                            )
                        }
                    },
                )

                // Imágenes anteriores al evento
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                ) {
                    Card(
                        modifier = Modifier.size(100.dp),
                        onClick = { showSheet = true },
                        colors = CardDefaults.cardColors(containerColor = White),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Upload images",
                                tint = Black,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = imagesScrollState,
                    ) {
                        items(selectedImagesUri) { uri ->
                            Box(modifier = Modifier.padding(4.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "selected image",
                                    modifier =
                                        Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.Gray, RoundedCornerShape(2.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                IconButton(
                                    onClick = {
                                        selectedImagesUri = selectedImagesUri - uri
                                    },
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                            .size(24.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "eliminar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        )
                    }
                    Button(
                        onClick = {
                            val dateTime = date.atTime(hour, minute)
                            onConfirm(name, location, dateTime, selectedImagesUri)
                            onDismiss()
                        },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = Color.White,
                            ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = "Crear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }

        // Ventana emergente para elegir proveniencia de la imagen
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                dragHandle = { BottomSheetDefaults.DragHandle() },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                ) {
                    ListItem(
                        headlineContent = { Text("Tomar Foto") },
                        leadingContent = {
                            Icon(Icons.Default.PhotoCamera, null)
                        },
                        modifier =
                            Modifier.clickable {
                                showSheet = false
                                if (cameraPermission.status.isGranted) {
                                    val file = createImageFile(context)
                                    val uri =
                                        FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file,
                                        )
                                    cameraImageUri.value = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    cameraPermission.launchPermissionRequest()
                                }
                            },
                    )
                    ListItem(
                        headlineContent = { Text("Elegir de la galería") },
                        leadingContent = {
                            Icon(Icons.Default.Image, null)
                        },
                        modifier =
                            Modifier.clickable {
                                showSheet = false
                                imagePickerLauncher.launch("image/*")
                            },
                    )
                }
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCreateEventPopUp() {
    CreateEventPopUp({}) { name, location, dateTime, image -> }
}
