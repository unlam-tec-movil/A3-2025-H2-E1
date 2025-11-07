package ar.edu.unlam.mobile.scaffolding.data.model

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
    val members: List<UserEntity>,
    val creator: UserEntity,
    val saved: List<Long>,
)
