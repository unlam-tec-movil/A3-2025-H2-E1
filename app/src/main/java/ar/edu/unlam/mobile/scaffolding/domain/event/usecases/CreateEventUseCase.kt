package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.domain.utils.Resource
import jakarta.inject.Inject

class CreateEventUseCase
    @Inject
    constructor(
        private val eventRepository: EventRepository,
    ) {
        suspend operator fun invoke(event: Event): Resource<Unit> = eventRepository.createEvent(event)
    }
