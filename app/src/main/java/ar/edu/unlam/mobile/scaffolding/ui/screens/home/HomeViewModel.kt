package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.CreateEventUseCase
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetEventByIdUseCase
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetMapEventsUseCase
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Coordinates
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Route
import ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories.NavigationRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.GetUserUseCase
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.MapProperties
import ar.edu.unlam.mobile.scaffolding.ui.model.EventDraft
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.HomeUIState
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.SearchUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getMapEvent: GetMapEventsUseCase,
        private val createEventUseCase: CreateEventUseCase,
        private val navigationRepository: NavigationRepository,
        private val getEventByIdUseCase: GetEventByIdUseCase,
        private val sessionManager: SessionManager,
        private val getUserUseCase: GetUserUseCase,
    ) : ViewModel() {
        // Mutable State Flow contiene un objeto de estado mutable. Simplifica la operación de
        // actualización de información y de manejo de estados de una aplicación: Cargando, Error, Éxito
        // (https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
        // _helloMessage State es el estado del componente "HelloMessage" inicializado como "Cargando"
        private val helloMessage = MutableStateFlow(MessageUIState.Loading)

        // _Ui State es el estado general del view model.
        private val _uiState = MutableStateFlow(HomeUIState(helloMessageState = helloMessage.value))

        // UIState expone el estado anterior como un Flujo de Estado de solo lectura.
        // Esto impide que se pueda modificar el estado desde fuera del ViewModel.
        val uiState = _uiState.asStateFlow()

        private val _searchUiState = MutableStateFlow(SearchUIState())
        val searchUiState = _searchUiState.asStateFlow()

        private val _currentRouteState = MutableStateFlow<Route?>(null)
        val currentRouteState = _currentRouteState.asStateFlow()

        private var mapEventJob: Job? = null
        private var navigationJob: Job? = null
        private var locationTimeoutJob: Job? = null

        val animationTime = 400

        init {
            _uiState.value = HomeUIState(helloMessageState = MessageUIState.Success("2b"))
            fetchEvents()
            loadLoggedUser()
        }

        private fun loadLoggedUser() {
            val userId = sessionManager.getLoggedUserId()
            if (userId == -1L) {
                _uiState.update {
                    it.copy(helloMessageState = MessageUIState.Error("Sesión inválida"))
                }
                return
            }
            viewModelScope.launch {
                getUserUseCase(userId).collect { resource ->
                    if (resource is Resource.Success) {
                        _uiState.update { it.copy(loggedUser = resource.data) }
                    }
                }
            }
        }

        fun setUserLocation(location: Location) {
            locationTimeoutJob?.cancel()
            locationTimeoutJob =
                viewModelScope.launch {
                    delay(6000L)
                    _uiState.update { it.copy(userLocation = null) }
                }
            val userLocation = GeoPoint(location.latitude, location.longitude)
            _uiState.update {
                it.copy(
                    userLocation = userLocation,
                )
            }
        }

        fun setTargetLocation(location: GeoPoint) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        mapProperties =
                            it.mapProperties.copy(
                                targetLocation = GeoPoint(location.latitude, location.longitude),
                            ),
                    )
                }
                delay(timeMillis = 300)
                _uiState.update { it.copy(mapProperties = it.mapProperties.copy(targetLocation = null)) }
            }
        }

        fun onMapPropertiesChanged(newProperties: MapProperties) {
            _uiState.update { currentState ->
                currentState.copy(mapProperties = newProperties)
            }
        }

        fun mapRotation(rotation: Float) {
            _uiState.update { currentState ->
                currentState.copy(
                    mapProperties =
                        currentState.mapProperties.copy(
                            mapOrientation = rotation,
                        ),
                )
            }
        }

        fun fetchEvents() {
            mapEventJob?.cancel()
            mapEventJob =
                viewModelScope.launch {
                    getMapEvent().collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _uiState.update {
                                    it.copy(eventList = result.data)
                                }
                            }

                            is Resource.Error -> {
                                _uiState.update {
                                    it.copy(helloMessageState = MessageUIState.Error(result.message))
                                }
                            }
                        }
                    }
                }
        }

        fun onSearch(searchResults: List<SuggestedEvent>) {
            _uiState.update {
                it.copy(eventList = searchResults)
            }
        }

        fun onEventSelected(event: SuggestedEvent) {
            _uiState.update { currentState ->
                currentState.copy(eventList = listOf(event))
            }
            fetchEventById(eventId = event.id)
        }

        fun onDraftChange(draft: EventDraft) {
            _uiState.update { it.copy(eventDraft = draft) }
        }

        fun onMapLongPress(location: GeoPoint?) {
            _uiState.update {
                it.copy(mapProperties = it.mapProperties.copy(longPressPoint = location))
            }
        }

        fun clearDraft() {
            _uiState.update {
                it.copy(
                    eventDraft = EventDraft(),
                    mapProperties = it.mapProperties.copy(longPressPoint = null),
                )
            }
        }

        fun createEvent() {
            val draft = _uiState.value.eventDraft
            viewModelScope.launch {
                val timestamp =
                    draft.dateTime!!
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                val imageString = draft.imagesUri.firstOrNull()?.toString()

                val newEvent =
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = draft.title,
                        description = draft.description,
                        dateTime = timestamp,
                        lat = draft.location!!.latitude,
                        lng = draft.location.longitude,
                        image = imageString,
                        beforeImage = emptyList(),
                        afterImage = null,
                        members = emptyList(),
                        creator = _uiState.value.loggedUser!!,
                        saved = false,
                        participating = false,
                    )
                val result = createEventUseCase(newEvent)
                when (result) {
                    is Resource.Success -> {
                        Log.d("HomeViewModel", "Evento creado correctamente")
                    }

                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error: ${result.message}")
                    }
                }
            }
        }

        fun getRoute(
            userCoordinates: Coordinates,
            eventCoordinates: Coordinates,
        ) {
            navigationJob?.cancel()
            navigationJob =
                viewModelScope.launch {
                    navigationRepository
                        .getRoute(
                            startLat = userCoordinates.lat,
                            endLat = eventCoordinates.lat,
                            startLon = userCoordinates.lon,
                            endLon = eventCoordinates.lon,
                        ).collectLatest { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _currentRouteState.value = result.data
                                    Log.d("ROUTE_DEBUG", "cantidad de puntos = ${result.data.coordinates.size}")
                                    Log.d("ROUTE_DEBUG", "primeros 5 puntos = ${result.data.coordinates.take(5)}")
                                    Log.d("ROUTE_DEBUG", "${result.data.distanceMeters} metros, ${result.data.durationMillis} ms")
                                }

                                is Resource.Error -> {
                                    Log.e("API call", result.message ?: "Error 400 - Bad Request")
                                }
                            }
                        }
                }
        }

        fun clearRoute() {
            _currentRouteState.value = null
        }

        fun fetchEventById(eventId: String) {
            viewModelScope.launch {
                getEventByIdUseCase(eventId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            setSelectedEvent(resource.data)
                            setTargetLocation(GeoPoint(resource.data.lat, resource.data.lng))
                        }
                        is Resource.Error -> {
                            Log.e("HomeViewModel", "Evento no encontrado: ${resource.message}")
                            _uiState.update { currentState ->
                                currentState.copy(selectedEvent = null)
                            }
                        }
                    }
                }
            }
        }

        fun clearSelectedEvent() {
            // 1. Ocultar la tarjeta
            _uiState.update { currentState ->
                currentState.copy(showEventCard = false)
            }

            // 2. Después de un tiempo, limpiar selectedEvent
            viewModelScope.launch {
                delay(animationTime.toLong())
                _uiState.update { currentState ->
                    currentState.copy(selectedEvent = null)
                }
            }
        }

        fun setSelectedEvent(event: EventItem) {
            // 1. Asignar el evento
            _uiState.update { currentState ->
                currentState.copy(selectedEvent = event)
            }

            // 2. Después de un tiempo, mostrar la tarjeta
            viewModelScope.launch {
                delay(100L)
                _uiState.update { currentState ->
                    currentState.copy(showEventCard = true)
                }
            }
        }
    }
