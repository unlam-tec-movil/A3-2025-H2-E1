package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor() {
        operator fun invoke(
            email: String,
            password: String,
        ): Boolean {
            if (email.isBlank() || password.isBlank()) return false
            if (!isValidEmail(email)) return false
            if (password.length < 6) return false
            return true
        }

        private fun isValidEmail(email: String): Boolean {
            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
            return emailRegex.matches(email)
        }
    }
