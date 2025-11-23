package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.AuthRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import jakarta.inject.Inject

class RegisterUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            email: String,
            password: String,
        ): Boolean {
            val userId = userRepository.registerUser(name)
            authRepository.register(userId, email, password)
            return true
        }
    }
