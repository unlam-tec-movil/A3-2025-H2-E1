package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventListUiState(
    val events: List<EventItem> = emptyList(),
    val currentState: MessageUIState = MessageUIState.Loading,
    val isDistance: Boolean = false,
)

@HiltViewModel
class EventListViewModel
    @Inject
    constructor(
        private val repository: EventRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(EventListUiState())
        val uiState = _uiState.asStateFlow()

        var userLocation: LatLng? = null

        init {
            getEvents()
        }

        fun updateFilter(isDistance: Boolean) {
            _uiState.update { it.copy(isDistance = isDistance) }
            viewModelScope.launch {
                getEvents()
            }
        }

        @SuppressLint("MissingPermission")
        fun getUserLocation(context: Context) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            userLocation = location?.let { LatLng(it.latitude, it.longitude) }

            if (_uiState.value.isDistance) sortEventsByDistance()
        }

        private fun sortEventsByDistance() {
            val location = userLocation ?: return
            _uiState.update { state ->
                state.copy(
                    events =
                        state.events.sortedBy { event ->
                            val result = FloatArray(1)
                            Location.distanceBetween(
                                location.latitude,
                                location.longitude,
                                event.lat,
                                event.lng,
                                result,
                            )
                            result[0]
                        },
                )
            }
        }

        private fun sortEventsByDate(order: String = "asc") {
            _uiState.update { state ->
                state.copy(
                    events =
                        if (order == "asc") {
                            state.events.sortedBy { it.dateTime }
                        } else {
                            state.events.sortedByDescending { it.dateTime }
                        },
                )
            }
        }

        fun getEvents(order: String = "asc") {
            viewModelScope.launch {
                _uiState.update { it.copy(currentState = MessageUIState.Loading) }

                try {
                    repository
                        .getEventsList(
                            sort = if (_uiState.value.isDistance) null else "date",
                            order = order,
                            size = null,
                        ).collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    val now = System.currentTimeMillis()

                                    // Filtrar solo futuros
                                    val futureEvents = resource.data.filter { it.dateTime > now }

                                    _uiState.update { state ->
                                        state.copy(
                                            events = futureEvents,
                                            currentState = MessageUIState.Success("Success"),
                                        )
                                    }

                                    if (_uiState.value.isDistance) {
                                        sortEventsByDistance()
                                    } else {
                                        sortEventsByDate(order = order)
                                    }
                                }

                                is Resource.Error -> {
                                    _uiState.update { state ->
                                        state.copy(currentState = MessageUIState.Error(resource.message))
                                    }
                                }
                            }
                        }
                } catch (e: Exception) {
                    _uiState.update { state ->
                        state.copy(currentState = MessageUIState.Error(e.message ?: "Error inesperado"))
                    }
                }
            }
        }
    }
