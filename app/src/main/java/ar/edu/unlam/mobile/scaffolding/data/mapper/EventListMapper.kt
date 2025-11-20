package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.EventItemEntity
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem

fun EventItemEntity.toEventItem(): EventItem =
    EventItem(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        image = imageUrl,
    )

fun EventItem.toEntity(): EventItemEntity =
    EventItemEntity(
        id = id,
        title = title,
        description = description,
        dateTime = dateTime,
        lat = lat,
        lng = lng,
        imageUrl = image,
    )
