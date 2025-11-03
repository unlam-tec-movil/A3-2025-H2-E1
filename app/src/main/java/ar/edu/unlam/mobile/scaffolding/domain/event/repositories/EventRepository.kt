package ar.edu.unlam.mobile.scaffolding.domain.event.repositories

import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun getSuggestedEvent(query: String): Flow<Resource<List<SuggestedEvent>>>

    suspend fun getEventsList(): Flow<Resource<List<EventList>>>

    suspend fun getEventList(id: Int): Flow<Resource<EventList>>

    suspend fun getEvent(id: Int): Flow<Resource<Event>>
}
