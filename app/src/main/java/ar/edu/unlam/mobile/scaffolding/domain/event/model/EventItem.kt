package ar.edu.unlam.mobile.scaffolding.domain.event.model

data class EventItem(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: Long,
    val lat: Double,
    val lng: Double,
    val image: String?,
)
