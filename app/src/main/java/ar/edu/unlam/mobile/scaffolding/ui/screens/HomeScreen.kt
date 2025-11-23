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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Coordinates
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.CurrentNavigationRouteInformationCard
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
import org.osmdroid.util.GeoPoint

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
    val currentRoute by viewModel.currentRouteState.collectAsState()

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var isSessionActive by remember { mutableStateOf(false) }
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
                        result.lastLocation?.let { viewModel.setUserLocation(it) }
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

    // Rotar el mapa cuando el usuario esté navegando a algún evento
    if (currentRoute != null) {
        val props = uiState.mapProperties
        viewModel.onMapPropertiesChanged(
            props.copy(
                rotationBySensor = !props.rotationBySensor,
                rotationByGesture = !props.rotationByGesture,
            ),
        )
    }

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
                    lat = null,
                    lng = null,
                    route = currentRoute?.coordinates?.map { GeoPoint(it.first, it.second) },
                    mapProperties = uiState.mapProperties,
                    onEventoClick = { evento ->
                        viewModel.fetchEventById(evento.id)
                    },
                    rotationChanged = viewModel::mapRotation,
                    onMapStateChanged = viewModel::onMapStateChanged,
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
                                onGetDirectionsClick = {
                                    viewModel.getRoute(
                                        userCoordinates =
                                            Coordinates(
                                                lat = uiState.userLocation?.latitude ?: 0.0,
                                                lon = uiState.userLocation?.longitude ?: 0.0,
                                            ),
                                        eventCoordinates =
                                            Coordinates(
                                                lat = event.lat,
                                                lon = event.lng,
                                            ),
                                    )
                                    showEventCard = false
                                    viewModel.clearSelectedEvent()
                                },
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
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(zIndex = 20f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearch = viewModel::onSearch,
                        onSuggestionSelected = { event ->
                            viewModel.onEventSelected(event)
                            viewModel.fetchEventById(event.id)
                        },
                        onActiveChange = viewModel::onActiveChange,
                    )

                    if (currentRoute != null) {
                        Spacer(modifier = Modifier.size(16.dp))

                        CurrentNavigationRouteInformationCard(
                            duration = currentRoute?.durationMillis ?: 0,
                            distance = currentRoute?.distanceMeters ?: 0.0,
                            onClosesClick = {
                                val props = uiState.mapProperties
                                viewModel.onMapPropertiesChanged(
                                    props.copy(
                                        rotationBySensor = false,
                                        rotationByGesture = true,
                                    ),
                                )

                                viewModel.clearRoute()
                            },
                        )
                    }

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
                        onClickAddEvent = { showCreateEventDialog = true },
                        onClickCenterMap = {
                            if (permissionState.status.isGranted) {
                                viewModel.onCenterRequest()
                            } else {
                                permissionState.launchPermissionRequest()
                            }
                        },
                    )
                }

                // --- Crear Evento ---
                if (showCreateEventDialog) {
                    CreateEventPopUp(
                        onDismiss = { showCreateEventDialog = false },
                        userLocation = uiState.userLocation,
                        onConfirm = { name, location, dateTime, imageUri ->
                            viewModel.createEvent(name, location, dateTime, imageUri)
                            showCreateEventDialog = false
                            viewModel.fetchEvents()
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
