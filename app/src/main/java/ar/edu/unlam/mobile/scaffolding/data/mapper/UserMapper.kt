package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.User

fun UserEntity.toUser(): User = User(id, name, avatarUrl, description)

fun User.toEntity(): UserEntity = UserEntity(id, name, avatarUrl, description)
