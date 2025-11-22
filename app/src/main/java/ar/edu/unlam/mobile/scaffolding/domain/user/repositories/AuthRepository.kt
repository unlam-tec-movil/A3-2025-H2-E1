package ar.edu.unlam.mobile.scaffolding.domain.user.repositories

import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserSession

interface AuthRepository {
    suspend fun login(userSession: UserSession): User?
}
