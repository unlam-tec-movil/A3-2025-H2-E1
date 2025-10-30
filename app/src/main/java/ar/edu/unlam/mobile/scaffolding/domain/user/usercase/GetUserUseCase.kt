package ar.edu.unlam.mobile.scaffolding.domain.user.usercase

import ar.edu.unlam.mobile.scaffolding.domain.user.model.User
import ar.edu.unlam.mobile.scaffolding.domain.user.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(id: Long): Flow<User> = userRepository.getUser(id)
    }
