package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventDetailsUiState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedParticipant: User? = null,
    val showParticipantPopup: Boolean = false,
    val isParticipating: Boolean = false,
)

@HiltViewModel
class EventDetailsViewModel
    @Inject
    constructor(
        private val eventRepository: EventRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val eventId: String = checkNotNull(savedStateHandle["id"])
        private val _uiState = MutableStateFlow(EventDetailsUiState(isLoading = true))
        val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

        init {
            loadEventDetails()
        }

        private fun loadEventDetails() {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                eventRepository.getEvent(eventId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    event = result.data,
                                    isLoading = false,
                                    isParticipating = false,
                                )
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isLoading = false,
                                )
                            }
                        }
                    }
                }
            }
        }

        fun onParticipantClick(user: User) {
            _uiState.update {
                it.copy(
                    selectedParticipant = user,
                    showParticipantPopup = true,
                )
            }
        }

        fun dismissParticipantPopup() {
            _uiState.update {
                it.copy(
                    showParticipantPopup = false,
                    selectedParticipant = null,
                )
            }
        }

        fun onParticipateClick() {
            // TODO: Lógica para unirse o salir del evento
            viewModelScope.launch {
                try {
                    val currentEvent = _uiState.value.event ?: return@launch

                    if (_uiState.value.isParticipating) {
                        // Lógica para SALIR del evento
                        // eventRepository.leaveEvent(currentEvent.id)
                        _uiState.update { it.copy(isParticipating = false) }
                    } else {
                        // Lógica para UNIRSE al evento
                        // eventRepository.joinedEventList(currentEvent.id)
                        _uiState.update { it.copy(isParticipating = true) }
                    }

                    // Opcional: Recargar el evento para ver los cambios reflejados (ej. nuevo participante en la lista)
                    // loadEventDetails(eventId)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Error al actualizar participación: ${e.message}") }
                }
            }
        }
    }
