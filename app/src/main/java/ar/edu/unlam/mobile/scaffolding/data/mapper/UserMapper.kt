package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem

fun UserEntity.toUserItem(): UserItem = UserItem(id, name, avatarUrl, description)

fun UserItem.toEntity(password: String): UserEntity = UserEntity(id, name, avatarUrl, description, password)
