package ar.edu.unlam.mobile.scaffolding.ui.common

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MessageUIState {
    data class Success(
        val message: String,
    ) : MessageUIState

    data object Loading : MessageUIState

    data class Error(
        val message: String,
    ) : MessageUIState
}
