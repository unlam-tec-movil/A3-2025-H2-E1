package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepositoryImpl
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val userRepository: UserRepositoryImpl,
        private val sessionManager: SessionManager,
    ) {
        operator fun invoke(
            name: String,
            password: String,
        ): Boolean {
            val user = userRepository.getUserByNameAndPassword(name, password)
            return if (user != null) {
                sessionManager.saveLogin(name)
                true
            } else {
                false
            }
        }
    }
