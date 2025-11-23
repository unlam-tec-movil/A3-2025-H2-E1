package ar.edu.unlam.mobile.scaffolding.data.model

data class EventItemEntity(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: Long,
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
)
