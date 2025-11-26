package ar.edu.unlam.mobile.scaffolding.ui.screens.home.state

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent

data class SearchUIState(
    val eventList: List<SuggestedEvent> = emptyList(),
    val currentQuery: String = "",
    val lastQuery: String = "",
    val isExpanded: Boolean = false,
    val searchState: EventSearchState = EventSearchState.Idle,
)
