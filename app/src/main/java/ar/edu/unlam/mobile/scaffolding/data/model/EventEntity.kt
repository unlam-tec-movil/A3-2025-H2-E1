package ar.edu.unlam.mobile.scaffolding.data.model

import java.util.Date

data class EventEntity(
    val eventId: String,
    val title: String,
    val description: String,
    val dateTime: Date,
    val lat: Double,
    val lng: Double,
    val imageUrl: String?,
    val beforeImageUrl: List<String>,
    val afterImageUrl: List<String>?,
    val membersId: List<Int>?,
    val creatorId: Int,
    val saved: Boolean,
    val participating: Boolean,
)
