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
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetSuggestedEventUseCase
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.utils.Resource
import ar.edu.unlam.mobile.scaffolding.ui.common.EventSearchState
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        private val getAutocompleteEvent: GetSuggestedEventUseCase,
        private val createEventUseCase: CreateEventUseCase,
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

        private var searchEventJob: Job? = null

        init {
            _uiState.value = HomeUIState(helloMessageState = MessageUIState.Success("2b"))
        }

        fun onSearchQueryChange(newQuery: String) {
            _searchUiState.update { currentState ->
                currentState.copy(currentQuery = newQuery)
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
                    .filter { query ->
                        query.isNotBlank() && query.length > 1 && query != _searchUiState.value.lastQuery
                    }.mapLatest { query ->
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
                                            // Para usar en HomeScreen o vm
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
                                currentQuery = currentState.lastQuery,
                            )
                        }
                    } else {
                        _searchUiState.update { currentState ->
                            currentState.copy(currentQuery = currentState.lastQuery)
                        }
                    }
                    searchEventJob?.cancel()
                }
            }
        }

        fun onSearch(searchQuery: String) {
            _searchUiState.update { currentState ->
                currentState.copy(
                    lastQuery = searchQuery,
                    currentQuery = searchQuery,
                    isExpanded = false,
                )
            }
            if (searchQuery.isNotBlank()) {
                _uiState.update { currentState ->
                    currentState.copy(
                        eventList = searchUiState.value.eventList,
                    )
                }
                Log.d("HomeViewModel", "onSearch: ${_uiState.value.eventList.size}")
                // TODO Mostrar los eventos de _uiState.eventList obtenidos en el mapa
            } else {
                // TODO("Si se descarta el query se tienen que obtener los eventos de forma normal")
            }
            searchEventJob?.cancel()
        }

        fun onEventSelected(event: SuggestedEvent) {
            _searchUiState.update { currentState ->
                currentState.copy(
                    lastQuery = event.title,
                    currentQuery = event.title,
                    isExpanded = false,
                )
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
            imageUri: Uri?,
        ) {
            viewModelScope.launch {
                val timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val imageString = imageUri?.toString()

                val user =
                    User(
                        id = 0L,
                        name = "",
                        avatarUrl = null,
                        description = null,
                    )

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
                        creator = user,
                        saved = false,
                        participating = false,
                    )
                createEventUseCase(newEvent)
            }
        }
    }
