package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserSession
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.LoginUseCase
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.state.LoginFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
    ) : ViewModel() {
        private val _formState = MutableStateFlow(LoginFormState())
        val formState: StateFlow<LoginFormState> = _formState
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

        fun onEmailChanged(email: String) {
            _formState.value = _formState.value.copy(email = email, emailError = null)
        }

        fun onPasswordChanged(password: String) {
            _formState.value = _formState.value.copy(password = password, passwordError = null)
        }

    fun onLoginClicked() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val success = loginUseCase(
                UserSession(
                    email = formState.value.email,
                    password = formState.value.password,
                )
            )

            isLoading = false

            if (success) {
                _formState.value = _formState.value.copy(isLoggedIn = true)
            } else {
                error = "Email o contraseña incorrectos"
            }
        }
    }
    }
