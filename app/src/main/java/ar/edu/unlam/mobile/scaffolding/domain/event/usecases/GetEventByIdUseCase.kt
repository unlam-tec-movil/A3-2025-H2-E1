package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventByIdUseCase
    @Inject
    constructor(
        private val repository: EventRepository,
    ) {
        suspend operator fun invoke(id: String): Flow<Resource<EventList>> = repository.getEventList(id)
    }
