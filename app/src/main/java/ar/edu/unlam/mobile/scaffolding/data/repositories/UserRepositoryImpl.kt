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
                    avatarUrl = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    description = "Desarrollador Android y entusiasta de Kotlin.",
                ),
                UserEntity(
                    id = 2,
                    name = "Ana García",
                    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80",
                    description = "Diseñadora UX/UI.",
                ),
                UserEntity(
                    id = 3,
                    name = "Carlos Martinez",
                    avatarUrl = "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    description = "Project Manager.",
                ),
                UserEntity(
                    id = 4,
                    name = "Sofía López",
                    avatarUrl = "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    description = "Especialista en QA.",
                ),
                UserEntity(
                    id = 5,
                    name = "David Gómez",
                    avatarUrl = "https://images.pexels.com/photos/837358/pexels-photo-837358.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                    description = "Analista de datos.",
                ),
            )

        override suspend fun getUser(userId: Long): Flow<User> =
            flow {
                val userEntity = mockUsers.find { it.id == userId } ?: mockUsers.first()
                emit(userEntity.toUser())
            }
    }
