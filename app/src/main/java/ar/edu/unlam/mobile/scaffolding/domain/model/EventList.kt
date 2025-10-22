package ar.edu.unlam.mobile.scaffolding.domain.model

import java.util.Date

data class EventList(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: Date,
    val lat: Double,
    val lng: Double,
    val image: String?,
)
