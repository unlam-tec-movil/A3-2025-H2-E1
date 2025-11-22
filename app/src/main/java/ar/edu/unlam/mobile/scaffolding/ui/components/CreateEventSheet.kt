package ar.edu.unlam.mobile.scaffolding.ui.components

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import ar.edu.unlam.mobile.scaffolding.utils.getAddressFromCoordinates
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.util.GeoPoint
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
fun CreateEventSheet(
    onDismiss: () -> Unit,
    userLocation: GeoPoint? = null,
    onConfirm: (String, String, GeoPoint, LocalDateTime, List<Uri>) -> Unit,
) {
    // Estados del evento
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationPoint by remember { mutableStateOf(userLocation) }
    var locationString by remember { mutableStateOf("") }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var hour by remember { mutableStateOf<Int?>(null) }
    var minute by remember { mutableStateOf<Int?>(null) }
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
    if (locationPoint != null) {
        LaunchedEffect(Unit) {
            locationString = getAddressFromCoordinates(context, locationPoint!!.latitude, locationPoint!!.longitude)
        }
    }
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier =
                Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Título
            Text(
                text = "Crear nuevo evento",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            // Nombre del evento
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = MaterialTheme.typography.bodyLarge,
                label = { Text("Nombre del evento") },
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Nombre del evento",
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                        Text("Nombre del evento")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                textStyle = MaterialTheme.typography.bodyLarge,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )

            Text("Ubicación del evento")
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación del evento",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    // Ubicación del evento
                    OutlinedTextField(
                        value = locationString,
                        onValueChange = { locationString = it },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                    )
                }
            }

            Text(
                text = "Fecha y hora del evento",
                style = MaterialTheme.typography.bodyLarge,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    // Seleccionar fecha del evento
                    Row(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { openDateDialog.value = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Fecha",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Column(
                            modifier =
                                Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f),
                        ) {
                            Text(
                                text = "Fecha del evento",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = date?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) ?: "Sin seleccionar",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (date == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                    )

                    // Seleccionar hora del evento
                    Row(
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { openTimeDialog.value = true },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Hora",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Column(
                            modifier =
                                Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f),
                        ) {
                            Text(
                                text = "Horario del evento",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text =
                                    if (hour != null && minute != null) {
                                        String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d hr",
                                            hour,
                                            minute,
                                        )
                                    } else {
                                        "Sin seleccionar"
                                    },
                                color = if (hour == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Imágenes anteriores al evento
            Text(
                text = "Imágenes del lugar",
                style = MaterialTheme.typography.bodyLarge,
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        state = imagesScrollState,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        item {
                            Card(
                                modifier = Modifier.size(100.dp),
                                onClick = { showSheet = true },
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    ),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier.size(50.dp),
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload images",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }
                        }
                        items(selectedImagesUri) { uri ->
                            Box(modifier = Modifier.padding(4.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "selected image",
                                    modifier =
                                        Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                IconButton(
                                    onClick = {
                                        selectedImagesUri = selectedImagesUri - uri
                                    },
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .background(
                                                Color.Black.copy(alpha = 0.5f),
                                                CircleShape,
                                            ).size(24.dp),
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
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Button(
                    enabled = name.isNotEmpty() && locationPoint != null && date != null && hour != null && minute != null,
                    onClick = {
                        val dateTime = date!!.atTime(hour!!, minute!!)
                        onConfirm(name, description, locationPoint!!, dateTime, selectedImagesUri)
                        onDismiss()
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                        ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Crear",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Ventana emergente para elegir proveniencia de la imagen
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
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

private fun createImageFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCreateEventSheet() {
    CreateEventSheet(
        onDismiss = {},
        onConfirm = { name, description, location, dateTime, image -> },
        userLocation =
            GeoPoint(
                -34.603738,
                -58.345,
            ),
    )
}
