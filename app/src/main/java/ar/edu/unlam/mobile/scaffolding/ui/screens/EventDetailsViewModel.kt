package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel
    @Inject
    constructor(
        private val repository: EventRepository,
    ) : ViewModel() {

        private val _eventState = MutableStateFlow<Resource<Event>>(Resource.Error(null, "Sin datos"))
        val eventState: StateFlow<Resource<Event>> = _eventState.asStateFlow()

        fun getEventById(
            eventId: Int,
            userId: Long,
        ) {
            viewModelScope.launch {
                repository.getEvent(eventId, userId).collect { result ->
                    _eventState.value = result
                }
            }
        }
    }
