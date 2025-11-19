package ar.edu.unlam.mobile.scaffolding.domain.event.repositories

import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun getMapEvents(): Flow<Resource<List<SuggestedEvent>>>

    suspend fun getSuggestedEvent(query: String): Flow<Resource<List<SuggestedEvent>>>

    suspend fun getEventsList(
        sort: String?,
        order: String?,
        size: Int?,
    ): Flow<Resource<List<EventList>>>

    suspend fun getJoinedEventsList(
        sort: String?,
        order: String?,
        userId: Long,
    ): Flow<Resource<List<EventList>>>

    suspend fun getEventList(id: String): Flow<Resource<EventList>>

    suspend fun getEvent(id: String): Flow<Resource<Event>>

    suspend fun createEvent(event: Event): Resource<Unit>
}
