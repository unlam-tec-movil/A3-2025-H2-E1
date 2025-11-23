package ar.edu.unlam.mobile.scaffolding.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.RegisterUserUseCase
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.ValidateConfirmPasswordUseCase
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.ValidateEmailUseCase
import ar.edu.unlam.mobile.scaffolding.domain.user.usercase.ValidatePasswordUseCase
import ar.edu.unlam.mobile.scaffolding.ui.screens.register.state.RegisterFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val validateEmailUseCase: ValidateEmailUseCase,
        private val validatePasswordUseCase: ValidatePasswordUseCase,
        private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase,
        private val registerUserUseCase: RegisterUserUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterFormState())
        val uiState: StateFlow<RegisterFormState> = _uiState

        fun onUsernameChange(value: String) {
            _uiState.value = _uiState.value.copy(usernameTextField = value)
        }

        fun onEmailChange(value: String) {
            _uiState.value = _uiState.value.copy(emailTextField = value)
        }

        fun onPasswordChange(value: String) {
            _uiState.value = _uiState.value.copy(passwordTextField = value)
        }

        fun onConfirmPasswordChange(value: String) {
            _uiState.value = _uiState.value.copy(confirmPasswordTextField = value)
        }

        fun onRegister() {
            val username = _uiState.value.usernameTextField
            val email = _uiState.value.emailTextField
            val password = _uiState.value.passwordTextField
            val confirmPassword = _uiState.value.confirmPasswordTextField

            viewModelScope.launch {
                val emailValidation = validateEmailUseCase(email)
                val passwordValidation = validatePasswordUseCase(password)
                val confirm = validateConfirmPasswordUseCase(password, confirmPassword)

                when {
                    !emailValidation.isValid ->
                        _uiState.value =
                            _uiState.value.copy(errorMessage = emailValidation.errorMessage)

                    !passwordValidation.isValid ->
                        _uiState.value =
                            _uiState.value.copy(errorMessage = passwordValidation.errorMessage)

                    !confirm.isValid ->
                        _uiState.value = _uiState.value.copy(errorMessage = confirm.errorMessage)

                    username.isBlank() ->
                        _uiState.value =
                            _uiState.value.copy(errorMessage = "El nombre no puede estar vacío.")

                    else -> {
                        registerUserUseCase(username, email, password)
                        _uiState.value =
                            _uiState.value.copy(
                                errorMessage = null,
                                isRegistered = true,
                            )
                    }
                }
            }
        }
    }
