package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toUser
import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
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
                UserEntity(
                    id = 4,
                    name = "Sofía López",
                    avatarUrl = "https://picsum.photos/id/1013/200",
                    description = "Especialista en QA.",
                ),
                UserEntity(
                    id = 5,
                    name = "David Gómez",
                    avatarUrl = "https://picsum.photos/id/1014/200",
                    description = "Analista de datos.",
                ),
            )

        override suspend fun getUser(userId: Long): Flow<User> =
            flow {
                val userEntity = mockUsers.find { it.id == userId } ?: mockUsers.first()
                emit(userEntity.toUser())
            }
    }
