package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.GetUserUseCase
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUIState(
    val name: String = "",
    val avatar: String? = "",
    val description: String? = "",
    val profileUiState: MessageUIState = MessageUIState.Loading,
)

@HiltViewModel
class UserProfileViewModel
    @Inject
    constructor(
        private val getUser: GetUserUseCase,
        private val sessionManager: SessionManager,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UserProfileUIState())
        val uiState = _uiState.asStateFlow()

    init {
        loadUserData()
    }

        fun loadUserData() {
            val userId = sessionManager.getLoggedUserId()

            if (userId == -1L) {
                _uiState.update {
                    it.copy(profileUiState = MessageUIState.Error("Sesión inválida"))
                }
                return
            }
            viewModelScope.launch {
                _uiState.update { it.copy(profileUiState = MessageUIState.Loading) }

                getUser(id = userId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    name = result.data.name,
                                    avatar = result.data.avatarUrl,
                                    description = result.data.description,
                                    profileUiState = MessageUIState.Success("Success"),
                                )
                            }
                        }

                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(profileUiState = MessageUIState.Error(result.message))
                            }
                        }
                    }
                }
            }
        }

        fun logout() {
            sessionManager.logout()
        }
    }
