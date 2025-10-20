package ar.edu.unlam.mobile.scaffolding.domain.repository

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}