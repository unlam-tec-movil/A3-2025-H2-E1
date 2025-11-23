package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetJoinedEventsListUseCase
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.GetUserUseCase
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserUIState(
    val name: String = "",
    val avatar: String? = "",
    val description: String? = "",
    val currentLocation: LatLng? = null,
    val joinedEvents: List<EventItem> = emptyList(),
    val pastEvents: List<EventItem> = emptyList(),
    val showPastEvents: Boolean = false,
    val userUiState: MessageUIState = MessageUIState.Loading,
)

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val getUser: GetUserUseCase,
        private val getJoinedEvents: GetJoinedEventsListUseCase,
    ) : ViewModel() {
        private val _userUiState = MutableStateFlow(UserUIState())
        val userUiState = _userUiState.asStateFlow()

        @SuppressLint("MissingPermission")
        fun getCurrentLocation(context: Context) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val userLocation =
                if (location != null) {
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(-34.6037, -58.3816) // Fallback: Obelisco
                }
            _userUiState.update {
                it.copy(currentLocation = userLocation)
            }
        }

        suspend fun getUser(userId: Long) {
            _userUiState.update { it.copy(userUiState = MessageUIState.Loading) }
            getUser(id = userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _userUiState.update { uIState ->
                            uIState.copy(
                                name = result.data.name,
                                avatar = result.data.avatarUrl,
                                description = result.data.description,
                                userUiState = MessageUIState.Success(message = "Success"),
                            )
                        }
                    }

                    is Resource.Error -> {
                        _userUiState.update {
                            it.copy(
                                userUiState = MessageUIState.Error(message = result.message),
                            )
                        }
                    }
                }
            }
        }

        private fun calculateDistance(
            startLatLng: LatLng,
            endLatLng: LatLng,
        ): Float {
            val result = FloatArray(1)
            Location.distanceBetween(
                startLatLng.latitude,
                startLatLng.longitude,
                endLatLng.latitude,
                endLatLng.longitude,
                result,
            )
            return result[0]
        }

        fun getEvents(
            userId: Long,
            sortBy: String? = "date",
            order: String? = "asc",
        ) {
            viewModelScope.launch {
                _userUiState.update { it.copy(userUiState = MessageUIState.Loading) }
                getJoinedEvents(userId = userId, sort = sortBy, order = order).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            var events = result.data
                            // Ordenamiento por distancia, la de "date" se hace desde el repo
                            if (sortBy == "distance" && _userUiState.value.currentLocation != null) {
                                val userLocation = _userUiState.value.currentLocation!!
                                events =
                                    events.sortedBy { event ->
                                        calculateDistance(userLocation, LatLng(event.lat, event.lng))
                                    }
                                if (order == "desc") {
                                    events = events.reversed()
                                }
                            }

                            // --- FILTRADO FUTUROS / PASADOS (USANDO dateTime) ---
                            val now = System.currentTimeMillis()
                            val upcoming = events.filter { it.dateTime >= now }
                            val past = events.filter { it.dateTime < now }

                            _userUiState.update {
                                it.copy(
                                    joinedEvents = upcoming, // por defecto mostramos futuros
                                    pastEvents = past,
                                    userUiState = MessageUIState.Success(message = "Success"),
                                )
                            }
                        }

                        is Resource.Error -> {
                            _userUiState.update {
                                it.copy(userUiState = MessageUIState.Error(message = result.message))
                            }
                        }
                    }
                }
            }
        }

        fun togglePastEvents() {
            _userUiState.update {
                it.copy(showPastEvents = !it.showPastEvents)
            }
        }
    }
