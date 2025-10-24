package ar.edu.unlam.mobile.scaffolding.ui.components

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventPopUp(
    onDismiss: () -> Unit,
    onConfirm: (String, String, LocalDateTime, Uri?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val openDateDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectedImageUri = uris
        }

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
                        openTimeDialog.value = true
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(20.dp)
                        .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Crear nuevo evento",
                    style = MaterialTheme.typography.headlineSmall,
                )

                // Event Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del evento") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Event Location
                OutlinedTextField(
                    value = name,
                    onValueChange = { location = it },
                    label = { Text("Ubicación del evento") },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Date and Time
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    onValueChange = {},
                    label = { Text("Fecha del evento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { openDateDialog.value = true }) {
                            Icons.Default.CalendarMonth
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "$hour:$minute hr",
                    onValueChange = {},
                    label = { Text("Horario del evento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { openTimeDialog.value = true }) {
                            Icons.Default.AccessTime
                        }
                    },
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Upload images",
                        )
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedImageUri) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "selected image",
                                modifier =
                                    Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .border(1.dp, Color.Gray, RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val dateTime = date.atTime(hour, minute)
                    // ToDo implementar botones
//                    onClick = {
//                        onConfirm(name, location, dateTime, selectedImageUri)
//                        onDismiss()
//                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewCreateEventPopUp() {
    CreateEventPopUp({}) { name, location, dateTime, image -> }
}
