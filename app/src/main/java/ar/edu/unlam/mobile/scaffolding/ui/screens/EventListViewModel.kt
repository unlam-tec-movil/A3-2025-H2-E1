package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    constructor() : ViewModel() {
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
                // Quitar delay para ver la lista en EventListScreenPreview
                delay(timeMillis = 1000L)
                _uiState.update { currentState ->
                    currentState.copy(
                        events = sampleEvents,
                        currentState = MessageUIState.Success("Success"),
                    )
                }
            }
        }
    }

private val sampleEvents =
    listOf(
        EventList(
            id = "1",
            image = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
            title = "Concierto de Rock",
            description = "Limpieza post concierto",
            dateTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24, // 1 día atrás
            lat = -34.5508002,
            lng = -58.4548101,
        ),
        EventList(
            id = "2",
            image = "https://shorturl.at/QUHmG",
            title = "Feria de Libro",
            description = "Limpieza post feria",
            dateTime = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2, // en 2 días
            lat = -34.641347,
            lng = -58.561187,
        ),
        EventList(
            id = "3",
            image = "https://shorturl.at/ZehlK",
            title = "Festival de Tecnología",
            description = "Limpieza post festival",
            dateTime = System.currentTimeMillis() + 1000L * 60 * 60 * 5, // en 5 horas
            lat = -34.6707531,
            lng = -58.5676761,
        ),
    )
