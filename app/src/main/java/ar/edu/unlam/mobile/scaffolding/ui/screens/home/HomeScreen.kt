package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import android.Manifest
import android.content.Context
import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Coordinates
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.AlertMessageBar
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventSheet
import ar.edu.unlam.mobile.scaffolding.ui.components.CurrentNavigationRouteInformationCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventHomeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingButtons
import ar.edu.unlam.mobile.scaffolding.ui.components.NearbyMap
import ar.edu.unlam.mobile.scaffolding.ui.components.SelectEventPositionBar
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
    searchViewModel: EventSearchViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchBarState by searchViewModel.searchUiState.collectAsState()
    val currentRoute by viewModel.currentRouteState.collectAsState()

    var showCreateEventDialog by remember { mutableStateOf(false) }
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
                    route = currentRoute?.coordinates?.map { GeoPoint(it.first, it.second) },
                    mapProperties = uiState.mapProperties,
                    onEventoClick = { evento ->
                        viewModel.fetchEventById(evento.id)
                    },
                    onLongPress = viewModel::onMapLongPress,
                    rotationChanged = viewModel::mapRotation,
                    userLocation = uiState.userLocation,
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .zIndex(15f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AnimatedVisibility(
                        visible = uiState.showEventCard,
                        enter =
                            slideInVertically(
                                initialOffsetY = { it }, // entra desde abajo
                                animationSpec = tween(durationMillis = viewModel.animationTime),
                            ) + fadeIn(animationSpec = tween(durationMillis = viewModel.animationTime)),
                        exit =
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(durationMillis = viewModel.animationTime),
                            ) + fadeOut(animationSpec = tween(durationMillis = viewModel.animationTime)),
                    ) {
                        uiState.selectedEvent?.let { event ->
                            EventHomeCard(
                                event = event,
                                distance =
                                    LatLng(
                                        uiState.userLocation?.latitude ?: 0.0,
                                        uiState.userLocation?.longitude ?: 0.0,
                                    ),
                                onGetDirectionsClick = {
                                    if (permissionState.status.isGranted) {
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
                                        viewModel.clearSelectedEvent()
                                    } else {
                                        permissionState.launchPermissionRequest()
                                    }
                                },
                                onViewEventClick = {
                                    navController.navigate("eventDetails/${event.id}")
                                    viewModel.clearSelectedEvent()
                                },
                                onCloseClick = {
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

                Column(modifier = Modifier.zIndex(20f)) {
                    // Barra de busqueda
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = searchViewModel::onSearchQueryChange,
                        onSearch = { query ->
                            searchViewModel.onSearch(query)
                            viewModel.onSearch(searchBarState.eventList)
                            if (query.isBlank()) {
                                viewModel.fetchEvents()
                            }
                        },
                        onSuggestionSelected = { event ->
                            searchViewModel.onSearch(event.title)
                            viewModel.onEventSelected(event)
                        },
                        onActiveChange = searchViewModel::onActiveChange,
                    )

                    // Informacion de la ruta seguida
                    if (currentRoute != null) {
                        Spacer(modifier = Modifier.size(16.dp))

                        CurrentNavigationRouteInformationCard(
                            duration = currentRoute?.durationMillis ?: 0,
                            distance = currentRoute?.distanceMeters ?: 0.0,
                            onClosesClick = {
                                viewModel.clearRoute()
                            },
                        )
                    }

                    // Ubicación seleccionada para crear evento
                    if (uiState.mapProperties.enableLongPress) {
                        Spacer(modifier = Modifier.size(16.dp))

                        SelectEventPositionBar(
                            currentLocation = uiState.mapProperties.longPressPoint ?: uiState.userLocation,
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.Start) {
                            // Cantidad de resultados de búsqueda
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

                            Spacer(modifier = Modifier.weight(1f))

                            // Brujula/rotacion actual del mapa
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
                        }
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
                        onClickAddEvent = {
                            showCreateEventDialog = true
                            viewModel.onMapPropertiesChanged(
                                newProperties = uiState.mapProperties.copy(enableLongPress = false),
                            )
                        },
                        onClickCenterMap = {
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
                        isSelectingLocation = uiState.mapProperties.enableLongPress,
                    )
                }

                // --- Crear Evento ---
                if (showCreateEventDialog) {
                    CreateEventSheet(
                        onDismiss = {
                            viewModel.clearDraft()
                            showCreateEventDialog = false
                        },
                        onConfirm = {
                            viewModel.createEvent()
                            showCreateEventDialog = false
                            viewModel.fetchEvents()
                        },
                        eventLocation = uiState.mapProperties.longPressPoint ?: uiState.userLocation,
                        onSelectEventPosition = {
                            viewModel.onMapPropertiesChanged(
                                uiState.mapProperties.copy(enableLongPress = true),
                            )
                            showCreateEventDialog = false
                        },
                        eventDraft = uiState.eventDraft,
                        onDraftChange = viewModel::onDraftChange,
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
