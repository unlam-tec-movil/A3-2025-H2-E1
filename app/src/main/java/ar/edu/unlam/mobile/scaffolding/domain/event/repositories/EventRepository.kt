package ar.edu.unlam.mobile.scaffolding.domain.event.repositories

import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
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
    ): Flow<Resource<List<EventItem>>>

    suspend fun getJoinedEventsList(
        sort: String?,
        order: String?,
        userId: Long,
    ): Flow<Resource<List<EventItem>>>

    suspend fun getEventList(id: String): Flow<Resource<EventItem>>

    suspend fun getEvent(
        id: String,
        userId: Long,
    ): Flow<Resource<Event>>

    suspend fun createEvent(event: Event): Resource<Unit>
}
