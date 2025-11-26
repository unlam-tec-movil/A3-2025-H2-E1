package ar.edu.unlam.mobile.scaffolding.ui.screens.user.state

import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState

data class UserProfileUIState(
    val name: String = "",
    val avatar: String? = "",
    val description: String? = "",
    val profileUiState: MessageUIState = MessageUIState.Loading,
)
