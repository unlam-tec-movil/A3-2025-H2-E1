package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.GetUserUseCase
import ar.edu.unlam.mobile.scaffolding.domain.utils.Resource
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class UserUIState(
    val name: String = "",
    val avatar: String? = "",
    val description: String? = "",
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
            _userUiState.update { it.copy(userUiState = MessageUIState.Loading) }
            userUseCase(id = userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _userUiState.update { uIState ->
                            uIState.copy(
                                name = result.data.name,
                                avatar = result.data.avatarUrl,
                                description = result.data.description,
                                userUiState = MessageUIState.Success(message = "Success"),
                            )
                        }
                    }

                    is Resource.Error -> {
                        _userUiState.update {
                            it.copy(
                                userUiState = MessageUIState.Error(message = result.message),
                            )
                        }
                    }
                }
            }
        }
    }
