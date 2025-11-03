package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toUser
import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor() : UserRepository {
        private val mockUsers =
            listOf(
                UserEntity(
                    id = 1,
                    name = "Juan Rodriguez",
                    avatarUrl = "https://picsum.photos/id/1005/200",
                    description = "Desarrollador Android y entusiasta de Kotlin.",
                ),
                UserEntity(
                    id = 2,
                    name = "Ana García",
                    avatarUrl = "https://picsum.photos/id/1011/200",
                    description = "Diseñadora UX/UI.",
                ),
                UserEntity(
                    id = 3,
                    name = "Carlos Martinez",
                    avatarUrl = "https://picsum.photos/id/1012/200",
                    description = "Project Manager.",
                ),
            )

        override suspend fun getUser(userId: Long): Flow<Resource<User>> =
            flow {
                val userEntity = mockUsers.find { it.id == userId }
                if (userEntity != null) {
                    emit(Resource.Success(data = userEntity.toUser()))
                } else {
                    emit(Resource.Error(message = "Usuario con ID $userId no encontrado."))
                }
            }
    }
