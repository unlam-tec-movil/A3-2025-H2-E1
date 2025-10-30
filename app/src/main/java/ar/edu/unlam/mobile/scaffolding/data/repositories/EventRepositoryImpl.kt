package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.mapper.toEventList
import ar.edu.unlam.mobile.scaffolding.data.mapper.toSuggestedEvent
import ar.edu.unlam.mobile.scaffolding.data.model.EventEntity
import ar.edu.unlam.mobile.scaffolding.data.model.EventListEntity
import ar.edu.unlam.mobile.scaffolding.data.model.SuggestedEventEntity
import ar.edu.unlam.mobile.scaffolding.data.model.UserEntity
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
        private val mockUsers =
            listOf(
                UserEntity(
                    1,
                    "Lucas Rodriguez",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
                    "Desarrollador Android.",
                ),
                UserEntity(
                    2,
                    "Ana García",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    "Diseñadora UX/UI.",
                ),
                UserEntity(
                    3,
                    "Carlos Martinez",
                    "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg",
                    "Project Manager.",
                ),
                UserEntity(
                    id = 4,
                    name = "Pepe Papa",
                    avatarUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBwr_zZjgvmu4BccwDNIHic8K5dyehw7cSYA&s",
                    description = null,
                ),
            )

        private val mockEvents =
            listOf(
                EventEntity(
                    eventId = "1",
                    title = "Limpieza de la Plaza San Martín",
                    description = "Nos juntamos a limpiar la plaza principal de la ciudad. Traer bolsas y guantes.",
                    dateTime = System.currentTimeMillis() - 86400000, // Ayer
                    lat = -34.6415,
                    lng = -58.5714,
                    imageUrl = "https://www.periodicoelbarrio.com.ar/wp-content/uploads/2021/04/pza-san-martin-vista-aerea-buenosaires.jpg",
                    beforeImageUrl = listOf("url_antes_1", "url_antes_2"),
                    afterImageUrl = null,
                    members = listOf(mockUsers[0], mockUsers[1]),
                    creator = mockUsers[2],
                    saved = false,
                    participating = true,
                ),
                EventEntity(
                    eventId = "2",
                    title = "Recolección de plásticos en la costa",
                    description = "Campaña para limpiar la costa del río de plásticos y otros residuos.",
                    dateTime = System.currentTimeMillis() + 86400000 * 2, // En 2 días
                    lat = -34.5425,
                    lng = -58.4555,
                    imageUrl = "https://www.diariodecuyo.com.ar/export/sites/diariodecuyo/img/2022/03/17/plastico_1.jpg_1584381838.jpg",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[1]),
                    creator = mockUsers[0],
                    saved = true,
                    participating = false,
                ),
                EventEntity(
                    eventId = "3",
                    title = "Jornada de reforestación en el parque",
                    description = "Plantaremos 50 árboles nativos en el parque ecológico de la ciudad.",
                    dateTime = System.currentTimeMillis() + 86400000 * 7, // En una semana
                    lat = -34.6707,
                    lng = -58.5627,
                    imageUrl = "https://www.lacumbre.gob.ar/wp-content/uploads/2022/08/REFORESTACION-EN-LA-CUMBRE-4.jpg",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = mockUsers,
                    creator = mockUsers[1],
                    saved = false,
                    participating = false,
                ),
                EventEntity(
                    eventId = "1",
                    title = "Concierto de Rock",
                    description = "Limpieza post concierto de rock con bandas locales e internacionales.",
                    dateTime = System.currentTimeMillis(),
                    imageUrl = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
                    lat = -34.5508002,
                    lng = -58.4548101,
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[1]),
                    creator = mockUsers[3],
                    saved = false,
                    participating = false,
                ),
            )

        override suspend fun getSuggestedEvent(query: String): Flow<List<SuggestedEvent>> =
            flow {
                val suggestedEvents =
                    mockEvents
                        .filter { eventEntity ->
                            eventEntity.title.contains(query, ignoreCase = true)
                        }.map { values ->
                            SuggestedEventEntity(
                                id = values.eventId,
                                title = values.title,
                                lat = values.lat,
                                lng = values.lng,
                            ).toSuggestedEvent()
                        }
                emit(suggestedEvents)
            }

        override suspend fun getEventsList(): Flow<List<EventList>> =
            flow {
                val eventList =
                    mockEvents.map { values ->
                        EventListEntity(
                            id = values.eventId,
                            title = values.title,
                            description = values.description,
                            dateTime = values.dateTime,
                            lat = values.lat,
                            lng = values.lng,
                            imageUrl = values.imageUrl,
                        ).toEventList()
                    }
                emit(eventList)
            }

        override suspend fun getEventList(id: Int): Flow<EventList> {
            TODO("Not yet implemented")
        }

        override suspend fun getEvent(id: Int): Flow<Event> {
            TODO("Not yet implemented")
        }
    }
