package ar.edu.unlam.mobile.scaffolding.ui.screens.user.state

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import com.google.android.gms.maps.model.LatLng

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
