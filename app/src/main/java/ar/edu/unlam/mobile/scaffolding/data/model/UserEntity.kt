package ar.edu.unlam.mobile.scaffolding.data.model

data class UserEntity(
    val id: Long,
    val name: String,
    val avatarUrl: String?,
    val description: String?,
    val password: String,
)
