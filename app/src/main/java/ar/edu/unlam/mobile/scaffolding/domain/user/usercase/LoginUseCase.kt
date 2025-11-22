package ar.edu.unlam.mobile.scaffolding.domain.user.usercase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserSession
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val sessionManager: SessionManager,
    ) {
    suspend operator fun invoke(userSession: UserSession): Boolean {
        val user = authRepository.login(userSession)

        return if (user != null) {
            sessionManager.saveLogin(userSession.email, user.id)
            true
        } else {
            false
        }
    }
    }
