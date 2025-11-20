package ar.edu.unlam.mobile.scaffolding.domain.user.repositories

import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: Long): Flow<Resource<UserItem>>
}
