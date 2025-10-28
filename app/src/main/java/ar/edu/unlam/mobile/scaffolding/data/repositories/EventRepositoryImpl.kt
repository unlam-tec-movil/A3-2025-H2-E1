package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toSuggestedEvent
import ar.edu.unlam.mobile.scaffolding.data.model.SuggestedEventEntity
import ar.edu.unlam.mobile.scaffolding.domain.event.model.Event
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventRepositoryImpl
    @Inject
    constructor() : EventRepository {
        override suspend fun getSuggestedEvent(query: String): Flow<List<SuggestedEvent>> {
            return flow {
                val mockSuggestedEvents =
                    listOf(
                        SuggestedEventEntity(
                            id = "1",
                            title = "Limpieza del parque $query",
                            lat = -34.5508002,
                            lng = -58.4548101,
                        ).toSuggestedEvent(),
                        SuggestedEventEntity(
                            id = "2",
                            title = "Se necesita quitar $query",
                            lat = -34.5508002,
                            lng = -58.4548101,
                        ).toSuggestedEvent(),
                        SuggestedEventEntity(
                            id = "3",
                            title = "Rio tapado por un $query ",
                            lat = -34.5508002,
                            lng = -58.4548101,
                        ).toSuggestedEvent(),
                    )
                emit(mockSuggestedEvents)
            }
            // TODO("Not yet implemented")
        }

        override suspend fun getEvents(): Flow<List<EventList>> {
            TODO("Not yet implemented")
        }

        override suspend fun getEventList(id: Int): Flow<EventList> {
            TODO("Not yet implemented")
        }

        override suspend fun getEvent(id: Int): Flow<Event> {
            TODO("Not yet implemented")
        }
    }
