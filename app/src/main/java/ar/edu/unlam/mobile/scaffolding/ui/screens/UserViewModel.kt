package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.GetUserUseCase
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class UserUIState(
    val name: String = "",
    val avatar: String = "",
    val description: String = "",
    val userUiState: MessageUIState = MessageUIState.Loading,
)

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userUseCase: GetUserUseCase,
    ) : ViewModel() {
        private val _userUiState = MutableStateFlow(UserUIState())
        val userUiState = _userUiState.asStateFlow()

        suspend fun getUser(userId: Long) {
            _userUiState.update {
                it.copy(userUiState = MessageUIState.Loading)
            }
            try {
                userUseCase(userId).collect { user ->
                    _userUiState.update {
                        it.copy(
                            name = user.name,
                            avatar = user.avatarUrl ?: "",
                            description = user.description ?: "",
                            userUiState = MessageUIState.Success(message = "Success"),
                        )
                    }
                }
            } catch (e: Exception) {
                _userUiState.update {
                    it.copy(userUiState = MessageUIState.Error(e.message ?: "Error"))
                }
            }
        }
    }
