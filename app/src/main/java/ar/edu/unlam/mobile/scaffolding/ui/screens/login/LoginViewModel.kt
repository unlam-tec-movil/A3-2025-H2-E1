package ar.edu.unlam.mobile.scaffolding.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.login.state.LoginFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState

    fun onEmailChanged(email: String) {
        _formState.value = _formState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun onPasswordChanged(password: String) {
        _formState.value = _formState.value.copy(
            password = password,
            passwordError = null
        )
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

        viewModelScope.launch {
            _formState.value = current.copy(isLoading = true, errorMessage = null)

            val result = userRepository.login(current.email, current.password)

            _formState.value = when {
                result.isSuccess -> current.copy(isLoading = false, isLoggedIn = true)
                else -> current.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Error"
                )
            }
        }
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El email es obligatorio"
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return if (!emailRegex.matches(email)) "Email inválido" else null
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es obligatoria"
        if (password.length < 6) return "Debe tener al menos 6 caracteres"
        return null
    }
}