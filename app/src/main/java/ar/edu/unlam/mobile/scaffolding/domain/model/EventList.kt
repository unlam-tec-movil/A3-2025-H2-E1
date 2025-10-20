package ar.edu.unlam.mobile.scaffolding.domain.model

data class EventList(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: String,
    val lat: Double,
    val lng: Double,
    val image: String?,
)
