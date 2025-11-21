package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.LoginUseCase
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.state.LoginFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val sessionManager: SessionManager,
    ) : ViewModel() {
        private val _formState = MutableStateFlow(LoginFormState())
        val formState: StateFlow<LoginFormState> = _formState

        fun onEmailChanged(email: String) {
            _formState.value = _formState.value.copy(email = email, emailError = null)
        }

        fun onPasswordChanged(password: String) {
            _formState.value = _formState.value.copy(password = password, passwordError = null)
        }

        fun onLoginClicked() {
            val current = _formState.value

            val success = loginUseCase(current.email, current.password)

            if (success) {
                // GUARDAMOS LOGIN
                sessionManager.saveLogin(current.email)

                _formState.value = current.copy(isLoggedIn = true)
            } else {
                _formState.value =
                    current.copy(
                        errorMessage = "Credenciales inválidas",
                    )
            }
        }
    }
