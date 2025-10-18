package ar.edu.unlam.mobile.scaffolding.ui.screens.login.state

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
)