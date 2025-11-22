package ar.edu.unlam.mobile.scaffolding.domain.user.model

data class UserItem(
    val id: Long,
    val name: String,
    val avatarUrl: String?,
    val description: String?,
)
