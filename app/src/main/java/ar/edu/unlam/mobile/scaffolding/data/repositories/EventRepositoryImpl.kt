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
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventRepositoryImpl
    @Inject
    constructor() : EventRepository {
        private val mockUsers =
            listOf(
                UserEntity(
                    id = 1,
                    name = "Juan Rodriguez",
                    avatarUrl = "https://picsum.photos/id/1005/200",
                    description = "Desarrollador Android y entusiasta de Kotlin.",
                ),
                UserEntity(
                    id = 2,
                    name = "Ana García",
                    avatarUrl = "https://picsum.photos/id/1011/200",
                    description = "Diseñadora UX/UI.",
                ),
                UserEntity(
                    id = 3,
                    name = "Carlos Martinez",
                    avatarUrl = "https://picsum.photos/id/1012/200",
                    description = "Project Manager.",
                ),
                UserEntity(
                    id = 4,
                    name = "Sofía López",
                    avatarUrl = "https://picsum.photos/id/1013/200",
                    description = "Especialista en QA.",
                ),
                UserEntity(
                    id = 5,
                    name = "David Gómez",
                    avatarUrl = "https://picsum.photos/id/1014/200",
                    description = "Analista de datos.",
                ),
                UserEntity(
                    6,
                    "Juan Pérez",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
                    "Desarrollador Android.",
                ),
                UserEntity(
                    7,
                    "María García",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
                    "Diseñadora UX/UI.",
                ),
                UserEntity(
                    8,
                    "Carlos Rodríguez",
                    "https://images.pexels.com/photos/614810/pexels-photo-614810.jpeg",
                    "Project Manager.",
                ),
                UserEntity(
                    9,
                    "Pepe Papa",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBwr_zZjgvmu4BccwDNIHic8K5dyehw7cSYA&s",
                    null,
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
                    imageUrl = "https://turismo.buenosaires.gob.ar/sites/turismo/files/plaza%20san%20martin%20panoramica_0.jpg",
                    beforeImageUrl =
                        listOf(
                            "https://tse4.mm.bing.net/th/id/OIP.qAaQAfjCzVnVppDL9yV4ngHaGH?rs=1&pid=ImgDetMain&o=7&rm=3",
                            "https://tse3.mm.bing.net/th/id/OIP.l1IS9Myg1X5DeUhv7RNSvgHaE8?rs=1&pid=ImgDetMain&o=7&rm=3",
                        ),
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
                    imageUrl = "https://blog.taranna.com/docs/reducir-plasticos-viaje-taranna-001-620x410.jpg",
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
                    imageUrl = "https://www.sedema.cdmx.gob.mx/storage/app/uploads/public/626/c8e/c30/626c8ec30db8d290091621.jpg",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = mockUsers,
                    creator = mockUsers[1],
                    saved = false,
                    participating = false,
                ),
                EventEntity(
                    eventId = "4",
                    title = "Concierto de Rock",
                    description = "Limpieza post concierto de rock con bandas locales e internacionales.",
                    dateTime = System.currentTimeMillis(),
                    imageUrl = "https://cdn.pixabay.com/photo/2014/07/09/12/17/live-concert-388160_1280.jpg",
                    lat = -34.550773663334006,
                    lng = -58.45480473855864,
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[1]),
                    creator = mockUsers[3],
                    saved = false,
                    participating = false,
                ),
                EventEntity(
                    eventId = "5",
                    title = "Mantenimiento en el Rosedal de Palermo",
                    description = "Jornada de cuidado de los rosales y limpieza de los caminos del Rosedal.",
                    dateTime = System.currentTimeMillis() + 86400000 * 3, // En 3 días
                    lat = -34.5701,
                    lng = -58.4115,
                    imageUrl = "https://bichosdecampo.com/wp-content/uploads/2023/08/17-1.jpg",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[3], mockUsers[4]),
                    creator = mockUsers[0],
                    saved = false,
                    participating = false,
                ),
                EventEntity(
                    eventId = "6",
                    title = "Limpieza en el Jardín Japonés",
                    description = "Ayudemos a mantener la belleza del Jardín Japonés, recogiendo hojas y basura menor.",
                    dateTime = System.currentTimeMillis() + 86400000 * 5, // En 5 días
                    lat = -34.5752,
                    lng = -58.4124,
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRzjpTqofLlQ1aciko3AWc3a23ZVn-tPtZRSw&s",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[0], mockUsers[2], mockUsers[4]),
                    creator = mockUsers[1],
                    saved = true,
                    participating = true,
                ),
                EventEntity(
                    eventId = "7",
                    title = "Clasificación de residuos en la Reserva Ecológica",
                    description = "Evento educativo para aprender y practicar la clasificación de residuos en la Costanera Sur.",
                    dateTime = System.currentTimeMillis() + 86400000 * 10, // En 10 días
                    lat = -34.6175,
                    lng = -58.3533,
                    imageUrl = "https://www.reservacostanera.com.ar/wp-content/uploads/2016/10/basura-NGG-20-10-16.jpg",
                    beforeImageUrl = listOf(),
                    afterImageUrl = null,
                    members = listOf(mockUsers[0]),
                    creator = mockUsers[4],
                    saved = false,
                    participating = false,
                ),
                EventEntity(
                    eventId = "8",
                    title = "Decoración y limpieza del Barrio Chino",
                    description = "Preparativos para el festival de la luna, decorando y limpiando las calles principales.",
                    dateTime = System.currentTimeMillis() + 86400000 * 4, // En 4 días
                    lat = -34.5458,
                    lng = -58.4552,
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAGDxzmN9kPnzEWZyQdkVjUpoK_AA-Noy_7Q&s",
                    beforeImageUrl =
                        listOf(
                            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAGDxzmN9kPnzEWZyQdkVjUpoK_AA-Noy_7Q&s",
                        ),
                    afterImageUrl = null,
                    members = listOf(mockUsers[1], mockUsers[2], mockUsers[3]),
                    creator = mockUsers[0],
                    saved = false,
                    participating = true,
                ),
                EventEntity(
                    eventId = "9",
                    title = "Cuidado del césped en el Planetario",
                    description = "Ayudemos a mantener el campo verde alrededor del Planetario Galileo Galilei.",
                    dateTime = System.currentTimeMillis() + 86400000 * 6, // En 6 días
                    lat = -34.5732,
                    lng = -58.4116,
                    imageUrl = "https://media.jardineriadelvalles.com/category/cuidados-del-cesped-1024x1024.jpeg?width=1200",
                    beforeImageUrl =
                        listOf(
                            "https://media.jardineriadelvalles.com/category/cuidados-del-cesped-1024x1024.jpeg?width=1200",
                        ),
                    afterImageUrl = null,
                    members = listOf(mockUsers[2], mockUsers[4]),
                    creator = mockUsers[3],
                    saved = true,
                    participating = false,
                ),
            )

        override suspend fun getMapEvents(): Flow<Resource<List<SuggestedEvent>>> =
            flow {
                val suggestedEvents =
                    mockEvents.map { values ->
                        SuggestedEventEntity(
                            id = values.eventId,
                            title = values.title,
                            lat = values.lat,
                            lng = values.lng,
                        ).toSuggestedEvent()
                    }
                emit(Resource.Success(suggestedEvents))
            }

        override suspend fun getSuggestedEvent(query: String): Flow<Resource<List<SuggestedEvent>>> =
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
                emit(Resource.Success(suggestedEvents))
            }

        override suspend fun getEventsList(): Flow<Resource<List<EventList>>> =
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
                emit(Resource.Success(eventList))
            }

        override suspend fun getEventList(id: Int): Flow<Resource<EventList>> {
            TODO("Not yet implemented")
        }

        override suspend fun getEvent(id: Int): Flow<Resource<Event>> {
            TODO("Not yet implemented")
        }
    }
