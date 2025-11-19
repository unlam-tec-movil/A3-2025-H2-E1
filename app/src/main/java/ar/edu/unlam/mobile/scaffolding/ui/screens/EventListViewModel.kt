package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventListUiState(
    val events: List<EventList> = emptyList(),
    val currentState: MessageUIState = MessageUIState.Loading,
    val isDistance: Boolean = true,
)

@HiltViewModel
class EventListViewModel
    @Inject
    constructor(
        private val eventRepository: EventRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(EventListUiState())

        val uiState = _uiState.asStateFlow()

        var userLocation: Location? = null

        init {
            getEvents()
        }

        fun updateFilter(isDistance: Boolean) {
            _uiState.update { currentState ->
                currentState.copy(
                    isDistance = isDistance,
                )
            }
            viewModelScope.launch {
                if (isDistance) {
                    sortEventsByDistance()
                } else {
                    sortEventsByDate()
                }
            }
        }

        private fun sortEventsByDistance() {
            _uiState.update { events ->
                events.copy(
                    events =
                        events.events.sortedBy { event ->
                            val result = FloatArray(1)
                            userLocation?.let {
                                Location.distanceBetween(
                                    userLocation!!.latitude,
                                    userLocation!!.longitude,
                                    event.lat,
                                    event.lng,
                                    result,
                                )
                                result[0]
                            }
                        },
                )
            }
        }

        private fun sortEventsByDate() {
            val now = System.currentTimeMillis()
            _uiState.update { state ->
                state.copy(
                    events =
                        state.events.sortedBy { event ->
                            val remaining = event.dateTime - now
                            if (remaining > 0L) remaining else Long.MAX_VALUE
                        },
                )
            }
        }

        @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
        fun getUserLocation(context: Context) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = location
                    }
                }
        }

        private fun getEvents() {
            viewModelScope.launch {
                eventRepository
                    .getEventsList(
                        order = "asc",
                        sort = if (_uiState.value.isDistance) "distance" else "date",
                        size = 20,
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        events = result.data,
                                        currentState = MessageUIState.Success("Success"),
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _uiState.update { currentState ->
                                    currentState.copy(
                                        currentState =
                                            MessageUIState.Error(
                                                result.message,
                                            ),
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
