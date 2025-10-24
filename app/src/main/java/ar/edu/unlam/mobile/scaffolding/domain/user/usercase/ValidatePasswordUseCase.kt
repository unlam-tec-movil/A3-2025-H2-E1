package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import javax.inject.Inject

class ValidatePasswordUseCase
    @Inject
    constructor() {
        operator fun invoke(password: String): ValidationResult {
            if (password.length < 6) {
                return ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
            }
            if (!password.any { it.isDigit() }) {
                return ValidationResult(false, "Debe contener al menos un número")
            }
            if (!password.any { it.isUpperCase() }) {
                return ValidationResult(false, "Debe contener al menos una mayúscula")
            }

            return ValidationResult(true)
        }
    }
