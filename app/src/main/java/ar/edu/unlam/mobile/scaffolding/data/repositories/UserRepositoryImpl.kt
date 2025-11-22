package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toUserItem
import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.data.model.UserSessionEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserSession
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.AuthRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
    @Inject
    constructor() : UserRepository, AuthRepository {
        private var mockUsers =
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

    private val mockCredentials =
        listOf(
            UserSessionEntity(
                userId = 1,
                email = "juan@gmail.com",
                password = "123",
            ),
            UserSessionEntity(
                userId = 2,
                email = "ana@gmail.com",
                password = "123",
            ),
            UserSessionEntity(
                userId = 3,
                email = "carlos@gmail.com",
                password = "123",
            ),
        )

        override suspend fun getUser(userId: Long): Flow<Resource<UserItem>> =
            flow {
                val userEntity = mockUsers.find { it.id == userId }
                if (userEntity != null) {
                    emit(Resource.Success(data = userEntity.toUserItem()))
                } else {
                    emit(Resource.Error(message = "Usuario con ID $userId no encontrado."))
                }
            }

    override suspend fun login(userSession: UserSession): User? {
        val credentialEntity =
            mockCredentials.find {
                it.email.equals(userSession.email, ignoreCase = true) &&
                        it.password == userSession.password
            } ?: return null

        val userEntity = mockUsers.find { it.id == credentialEntity.userId }
        return userEntity?.toUser()
    }
    }

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val avatarUrl: String,
    val description: String,
)
