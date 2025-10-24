package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import javax.inject.Inject

class ValidateConfirmPasswordUseCase
    @Inject
    constructor() {
        operator fun invoke(
            password: String,
            confirmPassword: String,
        ): ValidationResult {
            if (password != confirmPassword) {
                return ValidationResult(false, "Las contraseñas no coinciden")
            }
            return ValidationResult(true)
        }
    }
