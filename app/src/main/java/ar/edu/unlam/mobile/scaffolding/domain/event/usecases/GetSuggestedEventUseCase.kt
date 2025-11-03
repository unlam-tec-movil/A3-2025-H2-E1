package ar.edu.unlam.mobile.scaffolding.domain.event.usecases

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetSuggestedEventUseCase
    @Inject
    constructor(
        private val repository: EventRepository,
    ) {
        suspend operator fun invoke(query: String): Flow<Resource<List<SuggestedEvent>>> {
            if (query.length < 2) {
                return flowOf(Resource.Success(emptyList()))
            }
            return repository.getSuggestedEvent(query)
        }
    }
