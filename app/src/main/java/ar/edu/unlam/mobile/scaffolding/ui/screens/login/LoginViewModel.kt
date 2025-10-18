package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.state.LoginFormState
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _formState.value = _formState.value.copy(email = newEmail, emailError = null)
    }

    fun onPasswordChanged(newPassword: String) {
        _formState.value = _formState.value.copy(password = newPassword, passwordError = null)
    }

    fun onLoginClicked() {
        val current = _formState.value

        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)

        if (emailError != null || passwordError != null) {
            _formState.value = current.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }
    }
    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El email no puede estar vacío"
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        if (!regex.matches(email)) return "Formato de email inválido"
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña no puede estar vacía"
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        return null
    }
}