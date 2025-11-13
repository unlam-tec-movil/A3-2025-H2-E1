package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.AnimatedEventCard
import ar.edu.unlam.mobile.scaffolding.ui.components.CreateEventPopUp
import ar.edu.unlam.mobile.scaffolding.ui.components.Event
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.FloatingButtons
import ar.edu.unlam.mobile.scaffolding.ui.components.NearbyMap

const val HOME_SCREEN_ROUTE = "home"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchUiState.collectAsStateWithLifecycle()

    // cambiar por un EventList despues, y que sea un valor del uiState de paso
    var eventoSeleccionado by remember { mutableStateOf<SuggestedEvent?>(null) }

    var showCreateEventDialog by remember { mutableStateOf(false) }
    var isSessionActive by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    MapRotationSensor(
        enabled = uiState.mapProperties.rotationBySensor,
        sensorManager = sensorManager,
        currentOrientation = uiState.mapProperties.mapOrientation,
        onOrientationChanged = {
            viewModel.onMapPropertiesChanged(uiState.mapProperties.copy(mapOrientation = it))
        },
    )

    Box(modifier = modifier.fillMaxSize()) {
        when (val helloState = uiState.helloMessageState) {
            MessageUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is MessageUIState.Success -> {
                NearbyMap(
                    nearbyEvents = uiState.eventList,
                    modifier = Modifier.matchParentSize(),
                    mapProperties = uiState.mapProperties,
                    rotationChanged = { orientation ->
                        if (uiState.mapProperties.rotationByGesture) {
                            viewModel.onMapPropertiesChanged(uiState.mapProperties.copy(mapOrientation = orientation))
                        }
                    },
                    onEventoClick = { eventoSeleccionado = it },
                )

                //  Contenido encima del mapa (barra de búsqueda y saludo)
                // TODO llamar al "eventList" con la id del "suggestedEvent" del repositorio
                // De momento solo muestra los datos de suggestedEvent
                eventoSeleccionado?.let { evento ->
                    val eventCard =
                        Event(
                            id = evento.id,
                            name = evento.title,
                            dateTime = "01/12/2025 - 20:00 hs",
                            image1 = "https://picsum.photos/300/200",
                            image2 = "https://picsum.photos/301/200",
                            creatorId = 1,
                            lat = evento.lat,
                            lng = evento.lng,
                        )

                    AnimatedEventCard(eventCard = eventCard, onClose = { eventoSeleccionado = null })
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zIndex(zIndex = 20f),
                ) {
                    // barra de búsqueda
                    EventSearchBar(
                        searchUiState = searchBarState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onSearch = viewModel::onSearch,
                        onSuggestionSelected = { event ->
                            viewModel.onEventSelected(event)
                            eventoSeleccionado = event
                        },
                        onActiveChange = viewModel::onActiveChange,
                    )
                    Row {
                        // Cantidad de resultados de búsqueda
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

                        // Botón para cambiar modo de rotación y brujula
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
                        onClickStartSession = {
                            isSessionActive = !isSessionActive
                        },
                    )
                }
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
