package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toUser
import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor() : UserRepository {
        private var mockUsers =
            listOf(
                UserEntity(
                    id = 1,
                    name = "Juan Rodriguez",
                    avatarUrl = "https://picsum.photos/id/1005/200",
                    description = "Desarrollador Android y entusiasta de Kotlin.",
                    password = "123",
                ),
                UserEntity(
                    id = 2,
                    name = "Ana García",
                    avatarUrl = "https://picsum.photos/id/1011/200",
                    description = "Diseñadora UX/UI.",
                    password = "123",
                ),
                UserEntity(
                    id = 3,
                    name = "Carlos Martinez",
                    avatarUrl = "https://picsum.photos/id/1012/200",
                    description = "Project Manager.",
                    password = "123",
                ),
            )

        override suspend fun getUser(userId: Long): Flow<Resource<UserItem>> =
            flow {
                val userEntity = mockUsers.find { it.id == userId }
                if (userEntity != null) {
                    emit(Resource.Success(data = userEntity.toUser()))
                } else {
                    emit(Resource.Error(message = "Usuario con ID $userId no encontrado."))
                }
            }

        fun getUserByNameAndPassword(
            name: String,
            password: String,
        ): UserEntity? =
            mockUsers.find {
                it.name.equals(name, ignoreCase = true) && it.password == password
            }
    }

data class userDto(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val avatarUrl: String,
    val description: String,
)
