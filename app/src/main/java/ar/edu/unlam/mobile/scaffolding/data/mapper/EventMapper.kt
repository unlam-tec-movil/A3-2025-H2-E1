package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.EventEntity
import ar.edu.unlam.mobile.scaffolding.data.model.EventListEntity
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList

fun EventEntity.toEvent(userId: Long): Event =
    Event(
        id = eventId,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        image = imageUrl,
        beforeImage = beforeImageUrl,
        afterImage = afterImageUrl,
        members = members.map { it.toUser() },
        creator = creator.toUser(),
        saved = this.saved.any { it == userId },
        participating = this.members.any { it.id == userId },
    )

fun Event.toEntity(): EventEntity =
    EventEntity(
        eventId = id,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        imageUrl = image,
        beforeImageUrl = beforeImage,
        afterImageUrl = afterImage,
        members = members.map { it.toEntity() },
        creator = creator.toEntity(),
        // Esto en un caso real se manejaría con una operación de "update" o "save",
        // pero por simplicidad lo dejo así.
        saved = emptyList(),
    )

fun EventEntity.toEventList(): EventList =
    EventList(
        id = eventId,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        image = imageUrl,
    )

fun EventEntity.toEventListEntity(): EventListEntity =
    EventListEntity(
        id = eventId,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        imageUrl = imageUrl,
    )

fun List<Event>.toFutureEvents(): List<Event> {
    val now = System.currentTimeMillis()
    return this.filter { it.dateTime > now }
}
