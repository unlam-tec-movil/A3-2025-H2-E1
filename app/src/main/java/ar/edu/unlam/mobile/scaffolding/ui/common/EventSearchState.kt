package ar.edu.unlam.mobile.scaffolding.ui.common

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent

sealed interface EventSearchState {
    object Idle : EventSearchState

    object Loading : EventSearchState

    data class Success(
        val currentQuery: String,
        val events: List<SuggestedEvent>,
    ) : EventSearchState

    data class Error(
        val message: String?,
    ) : EventSearchState
}
