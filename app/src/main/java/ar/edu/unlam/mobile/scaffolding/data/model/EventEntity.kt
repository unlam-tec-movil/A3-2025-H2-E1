package ar.edu.unlam.mobile.scaffolding.data.model

import ar.edu.unlam.mobile.scaffolding.domain.user.model.User

data class EventEntity(
    val eventId: String,
    val title: String,
    val description: String,
    val dateTime: Long,
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
    val beforeImageUrl: List<String>,
    val afterImageUrl: List<String>?,
    val members: List<User>?,
    val creator: User,
    val saved: Boolean,
    val participating: Boolean,
)
