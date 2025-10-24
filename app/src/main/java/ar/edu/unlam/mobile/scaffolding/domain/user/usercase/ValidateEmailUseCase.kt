package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import javax.inject.Inject

class ValidateEmailUseCase
    @Inject
    constructor() {
        operator fun invoke(email: String): ValidationResult {
            if (email.isBlank()) return ValidationResult(false, "El campo no puede estar vacío")
            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {
                return ValidationResult(false, "Formato inválido")
            }
            return ValidationResult(true)
        }
    }

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
)
