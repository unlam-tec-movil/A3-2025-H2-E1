package ar.edu.unlam.mobile.scaffolding.viewmodels

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventListViewModel
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class EventListViewModelTest {
    private lateinit var repository: EventRepository
    private lateinit var viewModel: EventListViewModel

    private val mockEvents =
        listOf(
            EventItem(
                id = "1",
                title = "Limpieza de la Plaza San Martín",
                description = "Nos juntamos a limpiar la plaza principal de la ciudad. Traer bolsas y guantes.",
                dateTime = System.currentTimeMillis() + 86400000, // En 23 horas
                lat = -34.6415,
                lng = -58.5714,
                image = "https://turismo.buenosaires.gob.ar/sites/turismo/files/plaza%20san%20martin%20panoramica_0.jpg",
            ),
            EventItem(
                id = "2",
                title = "Recolección de plásticos en la costa",
                description = "Campaña para limpiar la costa del río de plásticos y otros residuos.",
                dateTime = System.currentTimeMillis() + 86400000 * 2, // En 2 días
                lat = -34.5425,
                lng = -58.4555,
                image = "https://blog.taranna.com/docs/reducir-plasticos-viaje-taranna-001-620x410.jpg",
            ),
        )

    fun getEvents(
        sort: String,
        order: String,
    ): Flow<Resource<List<EventItem>>> =
        flow {
            val sortedEvents =
                if (sort == "date") {
                    if (order == "asc") {
                        mockEvents.sortedBy { it.dateTime }
                    } else {
                        mockEvents.sortedByDescending { it.dateTime }
                    }
                } else {
                    mockEvents
                }

            emit(Resource.Success(sortedEvents))
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        repository = mockk()
        viewModel = EventListViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventsAscSortedByDate() {
        runTest {
            coEvery {
                repository.getEventsList("date", "asc", isNull())
            } returns getEvents("date", "asc")

            // Ejecutar VM
            viewModel.updateFilter(isDistance = false)
            viewModel.getEvents()

            // Esperar que terminen las coroutines
            advanceUntilIdle()

            // Validar
            assertEquals(
                "1",
                viewModel.uiState.value.events
                    .first()
                    .id,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventsDescSortedByDate() =
        runTest {
            coEvery {
                repository.getEventsList("date", "desc", isNull())
            } returns getEvents("date", "desc")

            // Ejecutar VM
            viewModel.updateFilter(isDistance = false)
            viewModel.getEvents(order = "desc")

            // Esperar que terminen las coroutines
            advanceUntilIdle()

            // Validar
            assertEquals(
                "2",
                viewModel.uiState.value.events
                    .first()
                    .id,
            )
        }
}
