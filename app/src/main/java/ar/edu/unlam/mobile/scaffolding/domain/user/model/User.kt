package ar.edu.unlam.mobile.scaffolding.domain.user.model

data class User(
    val id: Int,
    val name: String,
    val avatarUrl: String?,
    val description: String?,
)
