package ar.edu.unlam.mobile.scaffolding.data.model

import java.util.Date

data class EventListEntity(
    val id: String,
    val title: String,
    val description: String,
    val dateTime: Date,
    val lat: Double,
    val lng: Double,
    val image: String?,
)
