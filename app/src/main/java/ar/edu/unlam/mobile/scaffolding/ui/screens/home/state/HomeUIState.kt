package ar.edu.unlam.mobile.scaffolding.ui.screens.home.state

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.MapProperties
import ar.edu.unlam.mobile.scaffolding.ui.model.EventDraft
import org.osmdroid.util.GeoPoint

data class HomeUIState(
    val eventList: List<SuggestedEvent> = emptyList(),
    val selectedEvent: EventItem? = null,
    val eventDraft: EventDraft = EventDraft(),
    val mapProperties: MapProperties = MapProperties(),
    val userLocation: GeoPoint? = null,
    val showEventCard: Boolean = false,
    val helloMessageState: MessageUIState,
)
