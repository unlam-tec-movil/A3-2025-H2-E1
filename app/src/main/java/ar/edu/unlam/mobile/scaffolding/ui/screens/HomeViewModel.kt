package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.CreateEventUseCase
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetMapEventsUseCase
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetSuggestedEventsUseCase
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Coordinates
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Route
import ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories.NavigationRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.ui.common.EventSearchState
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.MapProperties
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

data class HomeUIState(
    val eventList: List<SuggestedEvent> = emptyList(),
    val mapProperties: MapProperties = MapProperties(),
    val helloMessageState: MessageUIState,
)

data class SearchUIState(
    val eventList: List<SuggestedEvent> = emptyList(),
    val currentQuery: String = "",
    val lastQuery: String = "",
    val isExpanded: Boolean = false,
    val searchState: EventSearchState = EventSearchState.Idle,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val getMapEvent: GetMapEventsUseCase,
        private val getAutocompleteEvent: GetSuggestedEventsUseCase,
        private val createEventUseCase: CreateEventUseCase,
        private val navigationRepository: NavigationRepository,
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
        private var searchEventJob: Job? = null
        private var navigationJob: Job? = null

        init {
            _uiState.value = HomeUIState(helloMessageState = MessageUIState.Success("2b"))
            fetchEvents()
        }

        fun onMapPropertiesChanged(newProperties: MapProperties) {
            _uiState.update { currentState ->
                currentState.copy(mapProperties = newProperties)
            }
        }

        private fun fetchEvents() {
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

        fun onSearchQueryChange(newQuery: String) {
            _searchUiState.update { currentState ->
                currentState.copy(currentQuery = newQuery)
            }
            if (newQuery.isEmpty()) {
                _searchUiState.update { currentState ->
                    currentState.copy(searchState = EventSearchState.Idle)
                }
            }
        }

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        private fun onSearchQueryObserve() {
            if (searchEventJob?.isActive == true) return
            searchEventJob =
                _searchUiState
                    .map { it.currentQuery }
                    .distinctUntilChanged()
                    .debounce(timeoutMillis = 500L)
                    .filter { query -> query.isNotBlank() && query.length > 1 }
                    .mapLatest { query ->
                        Log.d("HomeViewModel", "getSuggestionSearch: $query")
                        _searchUiState.update { it.copy(searchState = EventSearchState.Loading) }

                        getAutocompleteEvent(query).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _searchUiState.update {
                                        it.copy(
                                            searchState =
                                                EventSearchState.Success(
                                                    // Para usar en el componente EventSearchBar
                                                    currentQuery = query,
                                                    events = result.data,
                                                ),
                                            // Para usar en HomeScreen (en el mapa para ser mas exactos) o vm
                                            currentQuery = query,
                                            eventList = result.data,
                                        )
                                    }
                                }

                                is Resource.Error -> {
                                    _searchUiState.update {
                                        it.copy(searchState = EventSearchState.Error(result.message))
                                    }
                                    Log.e(
                                        "HomeViewModel",
                                        "Error al obtener sugerencias: ${result.message}",
                                    )
                                }
                            }
                        }
                    }.launchIn(viewModelScope)
        }

        fun onActiveChange(isActive: Boolean) {
            _searchUiState.update { it.copy(isExpanded = isActive) }
            when (isActive) {
                true -> onSearchQueryObserve()
                false -> {
                    if (_searchUiState.value.eventList.isEmpty()) {
                        _searchUiState.update { currentState ->
                            currentState.copy(
                                searchState = EventSearchState.Idle,
                                lastQuery = "",
                                currentQuery = "",
                            )
                        }
                        Log.d("HomeViewModel", "onActiveChange: eventList is empty")
                    }
                    searchEventJob?.cancel()
                }
            }
        }

        fun onSearch(searchQuery: String) {
            searchEventJob?.cancel()
            _searchUiState.update { currentState ->
                currentState.copy(
                    isExpanded = false,
                    lastQuery = searchQuery,
                )
            }
            if (searchQuery.isNotBlank()) {
                _uiState.update { currentState ->
                    currentState.copy(eventList = _searchUiState.value.eventList)
                }
                Log.d(
                    "HomeViewModel",
                    "onSearch: Mostrando ${_uiState.value.eventList.size} eventos en el mapa.",
                )
            } else {
                fetchEvents()
            }
        }

        fun onEventSelected(event: SuggestedEvent) {
            _searchUiState.update { currentState ->
                currentState.copy(
                    lastQuery = event.title,
                    currentQuery = event.title,
                    isExpanded = false,
                )
            }
            _uiState.update { currentState ->
                currentState.copy(eventList = listOf(event))
            }
            searchEventJob?.cancel()
            // TODO Abrir C3: EventHomeCard o mostrar en el mapa el evento seleccionado
            Log.d("HomeViewModel", "onEventSelected: ${event.title}")
            Log.d("HomeViewModel", "onEventSelected: ${event.id}, ${event.lat}, ${event.lng}")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createEvent(
            title: String,
            location: String,
            dateTime: LocalDateTime,
            imageUri: List<Uri>,
        ) {
            viewModelScope.launch {
                val timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val imageString = imageUri.firstOrNull()?.toString()

                val newEvent =
                    Event(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = "",
                        dateTime = timestamp,
                        lat = 0.0,
                        lng = 0.0,
                        image = imageString,
                        beforeImage = emptyList(),
                        afterImage = null,
                        members = emptyList(),
                        creator = User(0L, "Usuario", null, null),
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
                            endLon = userCoordinates.lon,
                        ).collectLatest { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _currentRouteState.value = result.data
                                }

                                is Resource.Error -> {
                                    Log.e("API call", result.message ?: "Error 400 - Bad Request")
                                }
                            }
                        }
                }
        }
    }
