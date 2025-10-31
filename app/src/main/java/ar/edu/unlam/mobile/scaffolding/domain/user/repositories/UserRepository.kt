package ar.edu.unlam.mobile.scaffolding.domain.user.repositories

import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: Long): Flow<Resource<User>>
}
