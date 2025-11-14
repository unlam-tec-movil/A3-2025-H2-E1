package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.EventHomeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingButtons
import ar.edu.unlam.mobile.scaffolding.ui.components.NearbyMap
import com.google.android.gms.location.*

const val HOME_SCREEN_ROUTE = "home"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToEvent: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var isSessionActive by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    // ---------------------------
    // UBICACIÓN DESDE HOME SCREEN
    // ---------------------------

    val fusedLocationClient =
        remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

    // Última ubicación conocida
    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { viewModel.setUserLocation(it) }
            }
        } catch (_: SecurityException) {
        }
    }

    // Actualizaciones periódicas (cada 3s)
    val locationRequest =
        remember {
            LocationRequest
                .Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    3000,
                ).build()
        }

    val locationCallback =
        remember {
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { viewModel.setUserLocation(it) }
                }
            }
        }

    DisposableEffect(Unit) {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper,
            )
        } catch (_: SecurityException) {
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // ---------------------------
    // MAP ROTATION SENSOR
    // ---------------------------

    MapRotationSensor(
        enabled = uiState.mapProperties.rotationBySensor,
        sensorManager = sensorManager,
        currentOrientation = uiState.mapProperties.mapOrientation,
        onOrientationChanged = { newAngle ->
            viewModel.onMapPropertiesChanged(
                uiState.mapProperties.copy(mapOrientation = newAngle),
            )
        },
    )

    // ======================
    // PANTALLA PRINCIPAL
    // ======================

    Box(modifier = modifier.fillMaxSize()) {
        when (val helloState = uiState.helloMessageState) {
            MessageUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is MessageUIState.Success -> {
                // --- MAPA ---
                NearbyMap(
                    nearbyEvents = uiState.eventList,
                    modifier = Modifier.matchParentSize(),
                    mapProperties = uiState.mapProperties,
                    onMapRotationChanged = { orientation ->
                        if (!uiState.mapProperties.rotationBySensor) {
                            viewModel.onMapPropertiesChanged(
                                uiState.mapProperties.copy(mapOrientation = orientation),
                            )
                        }
                    },
                    onEventoClick = { evento ->
                        viewModel.fetchEventById(evento.id.toInt())
                    },
                )

                // Estado para animar tarjeta del evento seleccionado
                var showEventCard by remember { mutableStateOf(false) }

                LaunchedEffect(selectedEvent) {
                    showEventCard = selectedEvent != null
                }

                selectedEvent?.let { event ->

                    val distanceText =
                        userLocation?.let { userLoc ->
                            val eventLocation =
                                android.location.Location("").apply {
                                    latitude = event.lat
                                    longitude = event.lng
                                }
                            val distanceMeters = userLoc.distanceTo(eventLocation)
                            if (distanceMeters >= 1000) {
                                "${"%.1f".format(distanceMeters / 1000)} km"
                            } else {
                                "${distanceMeters.toInt()} mts"
                            }
                        } ?: "Calculando..."

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedVisibility(
                            visible = showEventCard,
                            enter =
                                expandVertically(
                                    expandFrom = Alignment.Bottom,
                                    animationSpec = tween(durationMillis = 300),
                                ) + fadeIn(animationSpec = tween(durationMillis = 300)),
                            exit =
                                shrinkVertically(
                                    shrinkTowards = Alignment.Bottom,
                                    animationSpec = tween(durationMillis = 300),
                                ) + fadeOut(animationSpec = tween(durationMillis = 300)),
                        ) {
                            EventHomeCard(
                                event = event,
                                distance = distanceText,
                                onViewEventClick = {
                                    showEventCard = false
                                    onNavigateToEvent(event.id.toInt()) // <<--- NAVEGACIÓN
                                    viewModel.clearSelectedEvent()
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 16.dp),
                            )
                        }
                    }
                }

                // --- Búsqueda ---
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(20f),
                ) {
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearch = viewModel::onSearch,
                        onSuggestionSelected = { event ->
                            viewModel.onEventSelected(event)
                            viewModel.fetchEventById(event.id.toInt())
                        },
                        onActiveChange = viewModel::onActiveChange,
                    )

                    Row {
                        AnimatedVisibility(searchBarState.lastQuery.isNotEmpty()) {
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            ) {
                                Text(
                                    text =
                                        if (uiState.eventList.size > 1) {
                                            "Resultados de búsqueda: ${uiState.eventList.size}"
                                        } else {
                                            "Resultado de búsqueda"
                                        },
                                    modifier = Modifier.padding(horizontal = 6.dp),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        FloatingActionButton(
                            onClick = {
                                val props = uiState.mapProperties
                                viewModel.onMapPropertiesChanged(
                                    props.copy(
                                        rotationBySensor = !props.rotationBySensor,
                                        rotationByGesture = !props.rotationByGesture,
                                    ),
                                )
                            },
                            containerColor =
                                if (uiState.mapProperties.rotationBySensor) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainer
                                },
                            shape = CircleShape,
                            modifier =
                                Modifier
                                    .size(52.dp)
                                    .padding(12.dp),
                        ) {
                            val animatedOrientation by animateFloatAsState(
                                targetValue = uiState.mapProperties.mapOrientation,
                                animationSpec = tween(300, easing = LinearOutSlowInEasing),
                            )
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Cambiar modo de rotación",
                                modifier = Modifier.rotate(animatedOrientation),
                            )
                        }
                    }
                }

                // --- Botones Flotantes ---
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                ) {
                    FloatingButtons(
                        isSessionActive = isSessionActive,
                        onClickCamera = { },
                        onClickAddEvent = { showCreateEventDialog = true },
                        onClickStartSession = { isSessionActive = !isSessionActive },
                    )
                }

                // --- Crear Evento ---
                if (showCreateEventDialog) {
                    CreateEventPopUp(
                        onDismiss = { showCreateEventDialog = false },
                        onConfirm = { name, location, dateTime, imageUri ->
                            viewModel.createEvent(name, location, dateTime, imageUri)
                            showCreateEventDialog = false
                        },
                    )
                }
            }

            is MessageUIState.Error -> {
                Text(
                    text = helloState.message,
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun MapRotationSensor(
    enabled: Boolean,
    sensorManager: SensorManager,
    currentOrientation: Float,
    onOrientationChanged: (Float) -> Unit,
) {
    DisposableEffect(enabled) {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (enabled && rotationSensor != null) {
            val listener =
                object : SensorEventListener {
                    private var lastSmoothed = currentOrientation
                    private val alpha = 0.2f

                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                            val rotationMatrix = FloatArray(9)
                            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(rotationMatrix, orientation)

                            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                            val diff = ((azimuth - lastSmoothed + 540f) % 360f) - 180f
                            lastSmoothed += alpha * diff

                            onOrientationChanged(lastSmoothed)
                        }
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int,
                    ) {}
                }

            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)

            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            onDispose {}
        }
    }
}
