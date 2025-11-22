package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.AlertMessageBar
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventSheet
import ar.edu.unlam.mobile.scaffolding.ui.components.EventHomeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingButtons
import ar.edu.unlam.mobile.scaffolding.ui.components.NearbyMap
import ar.edu.unlam.mobile.scaffolding.ui.components.SystemBarStyle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

const val HOME_SCREEN_ROUTE = "home"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsState()

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var isSessionActive by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    SystemBarStyle(searchBarState.isExpanded)

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // UBICACIÓN DESDE HOME SCREEN
    // Se pide el permiso de ubicación al entrar a la screen
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    // Con esto se obtiene la ubicación inicial
    LaunchedEffect(key1 = permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { it?.let(viewModel::setUserLocation) }
            } catch (_: SecurityException) {
            }
        }
    }

    // Esto lo que hace es obtener la ubicacion a tiempo real, esta pensado para el mapa
    DisposableEffect(key1 = permissionState.status.isGranted) {
        if (!permissionState.status.isGranted) {
            onDispose { }
        } else {
            val locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { viewModel::setUserLocation }
                    }
                }

            val locationRequest =
                LocationRequest
                    .Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        3000L,
                    ).build()

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper,
                )
            } catch (_: SecurityException) {
            }

            onDispose {
                try {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                } catch (_: Exception) {
                }
            }
        }
    }

    // MAP ROTATION SENSOR
    MapRotationSensor(
        enabled = uiState.mapProperties.rotationBySensor,
        sensorManager = sensorManager,
        currentOrientation = uiState.mapProperties.mapOrientation,
        onOrientationChanged = {
            viewModel.onMapPropertiesChanged(uiState.mapProperties.copy(mapOrientation = it))
        },
    )

    // PANTALLA PRINCIPAL
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
                    onEventoClick = { evento ->
                        viewModel.fetchEventById(evento.id)
                    },
                    onLongPress = viewModel::onMapLongPress,
                    rotationChanged = viewModel::mapRotation,
                    userLocation = uiState.userLocation,
                )

                // Estado para animar tarjeta del evento seleccionado
                var showEventCard by remember { mutableStateOf(false) }

                LaunchedEffect(selectedEvent) {
                    showEventCard = selectedEvent != null
                }

                selectedEvent?.let { event ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .zIndex(15f),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedVisibility(
                            visible = showEventCard,
                            enter =
                                expandVertically(
                                    expandFrom = Alignment.Bottom,
                                    animationSpec = tween(durationMillis = 1000),
                                ) + fadeIn(animationSpec = tween(durationMillis = 1000)),
                            exit =
                                shrinkVertically(
                                    shrinkTowards = Alignment.Bottom,
                                    animationSpec = tween(durationMillis = 1000),
                                ) + fadeOut(animationSpec = tween(durationMillis = 1000)),
                        ) {
                            EventHomeCard(
                                event = event,
                                distance =
                                    LatLng(
                                        uiState.userLocation?.latitude ?: 0.0,
                                        uiState.userLocation?.longitude ?: 0.0,
                                    ),
                                onViewEventClick = {
                                    showEventCard = false
                                    navController.navigate("eventDetails/${event.id}")
                                    viewModel.clearSelectedEvent()
                                },
                                onCloseClick = {
                                    // Oculta la tarjeta
                                    showEventCard = false
                                    // Limpia el evento seleccionado
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
                EventSearchBar(
                    searchUiState = searchBarState,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSearch = viewModel::onSearch,
                    onSuggestionSelected = { event ->
                        viewModel.onEventSelected(event)
                    },
                    onActiveChange = viewModel::onActiveChange,
                )

                // Cantidad de resultados de búsqueda
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 90.dp)
                            .align(Alignment.TopStart),
                ) {
                    AnimatedVisibility(searchBarState.lastQuery.isNotEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                }

                // Botones de acción para el mapa
                Column(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 90.dp),
                    horizontalAlignment = Alignment.End,
                ) {
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
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Cambiar modo de rotación",
                            modifier = Modifier.rotate(uiState.mapProperties.mapOrientation),
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            if (permissionState.status.isGranted) {
                                uiState.userLocation?.let { currentLocation ->
                                    viewModel.setTargetLocation(currentLocation)
                                } ?: run {
                                    showAlert = true
                                }
                            } else {
                                permissionState.launchPermissionRequest()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = CircleShape,
                        modifier =
                            Modifier
                                .size(52.dp)
                                .padding(12.dp),
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Centrar en mi posición",
                        )
                    }
                }

                // Botones para crear evento y camara
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                ) {
                    FloatingButtons(
                        isSessionActive = isSessionActive,
                        onClickCamera = { },
                        onClickAddEvent = { showCreateEventDialog = true },
                        onClickCenterMap = {
                            isSessionActive = !isSessionActive
                        },
                    )
                }

                // --- Crear Evento ---
                if (showCreateEventDialog) {
                    CreateEventSheet(
                        onDismiss = { showCreateEventDialog = false },
                        userLocation = uiState.mapProperties.longPressPoint ?: uiState.userLocation,
                        onConfirm = { name, description, location, dateTime, imageUri ->
                            viewModel.createEvent(name, description, location, dateTime, imageUri)
                            showCreateEventDialog = false
                            viewModel.fetchEvents()
                        },
                    )
                }

                AlertMessageBar(
                    message = "Ubicación no disponible. GPS desactivado.",
                    showAlert = showAlert,
                    changeShowAlert = { showAlert = false },
                )
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
        val rotationSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                ?: return@DisposableEffect onDispose {}

        if (enabled) {
            val listener =
                object : SensorEventListener {
                    private var lastOrientation = currentOrientation
                    private val alpha = 0.1f

                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                            val rotationMatrix = FloatArray(9)
                            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(rotationMatrix, orientation)

                            // Convierte la orientación a grados
                            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                            // Esto cambia el eje de sentido horario a antihorario para que la brujula apunte
                            // bien el este y el oeste, pero da problemas al poner la app en vertical.
                            azimuth = (360 - azimuth) % 360

                            val diff = ((azimuth - lastOrientation + 540f) % 360f) - 180f
                            lastOrientation += alpha * diff

                            onOrientationChanged(lastOrientation)
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
