package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.EventEntity
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event

fun EventEntity.toEvent(): Event =
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
        membersId = membersId,
        creatorId = creatorId,
        saved = saved,
        participating = participating,
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
        membersId = membersId,
        creatorId = creatorId,
        saved = saved,
        participating = participating,
    )
