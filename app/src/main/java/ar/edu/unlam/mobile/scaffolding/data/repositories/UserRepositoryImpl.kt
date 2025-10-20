package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.delay

class UserRepositoryImpl @Inject constructor() : UserRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1000)

        return if (email == "test@test.com" && password == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Credenciales inválidas"))
        }
    }
}