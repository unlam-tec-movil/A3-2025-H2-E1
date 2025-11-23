package ar.edu.unlam.mobile.scaffolding.ui.screens.register.state

data class RegisterFormState(
    val emailTextField: String = "",
    val usernameTextField: String = "",
    val passwordTextField: String = "",
    val confirmPasswordTextField: String = "",
    val errorMessage: String? = null,
    val isRegistered: Boolean = false,
)
