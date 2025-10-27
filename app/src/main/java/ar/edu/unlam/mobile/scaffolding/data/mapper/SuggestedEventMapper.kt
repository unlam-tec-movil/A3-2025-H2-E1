package ar.edu.unlam.mobile.scaffolding.data.mapper

import ar.edu.unlam.mobile.scaffolding.data.model.SuggestedEventEntity
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent

fun SuggestedEventEntity.toSuggestedEvent(): SuggestedEvent =
    SuggestedEvent(
        id = id,
        title = title,
        lat = lat,
        lng = lng,
    )

fun SuggestedEvent.toEntity(): SuggestedEventEntity =
    SuggestedEventEntity(
        id = id,
        title = title,
        lat = lat,
        lng = lng,
    )
