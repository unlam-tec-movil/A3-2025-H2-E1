package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSuggestedEventUseCase
    @Inject
    constructor(
        private val repository: EventRepository,
    ) {
        suspend operator fun invoke(query: String): Flow<List<SuggestedEvent>> {
            if (query.length < 3) {
                return flowOf(emptyList())
            }
            return repository.getSuggestedEvent(query).map { it }
        }
    }
