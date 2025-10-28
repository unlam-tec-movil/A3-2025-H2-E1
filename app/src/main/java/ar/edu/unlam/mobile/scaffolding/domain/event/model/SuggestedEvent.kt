package ar.edu.unlam.mobile.scaffolding.domain.event.model

data class SuggestedEvent(
    val id: String,
    val title: String,
    val lat: Double,
    val lng: Double,
)
