package ar.edu.unlam.mobile.scaffolding.domain.model

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
    val membersId: List<Int>?,
    val creatorId: Int,
    val saved: Boolean,
    val participating: Boolean,
)
