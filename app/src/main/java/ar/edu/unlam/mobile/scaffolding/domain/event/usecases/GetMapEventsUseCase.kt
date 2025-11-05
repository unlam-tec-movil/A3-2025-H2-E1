package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMapEventsUseCase
    @Inject
    constructor(
        private val eventsRepository: EventRepository,
    ) {
        suspend operator fun invoke(): Flow<Resource<List<SuggestedEvent>>> = eventsRepository.getMapEvents()
    }
