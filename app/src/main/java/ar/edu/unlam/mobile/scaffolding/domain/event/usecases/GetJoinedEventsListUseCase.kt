package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetJoinedEventsListUseCase
    @Inject
    constructor(
        private val eventRepository: EventRepository,
    ) {
        suspend operator fun invoke(
            userId: Long,
            sort: String?,
            order: String?,
        ): Flow<Resource<List<EventList>>> = eventRepository.getJoinedEventsList(userId = userId, sort = sort, order = order)
    }
