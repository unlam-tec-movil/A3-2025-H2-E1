package ar.edu.unlam.mobile.scaffolding.domain.event.model

import ar.edu.unlam.mobile.scaffolding.domain.user.model.User

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: Long,
    val lat: Double,
    val lng: Double,
    val image: String?,
    val beforeImage: List<String>,
    val afterImage: List<String>?,
    val members: List<User>?,
    val creator: User,
    val saved: Boolean,
    val participating: Boolean,
)
