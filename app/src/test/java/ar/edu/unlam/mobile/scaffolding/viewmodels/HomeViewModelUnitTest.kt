package ar.edu.unlam.mobile.scaffolding.viewmodels

import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.*
import ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories.NavigationRepository
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.HomeViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.EventSearchState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeViewModelUnitTest {
    private lateinit var viewModel: HomeViewModel

    // Dependencias simuladas para poder instanciar el viewmodel
    private val getMapEventsUseCase: GetMapEventsUseCase = mockk(relaxed = true)
    private val getSuggestedEventsUseCase: GetSuggestedEventsUseCase = mockk(relaxed = true)
    private val createEventUseCase: CreateEventUseCase = mockk(relaxed = true)
    private val navigationRepository: NavigationRepository = mockk(relaxed = true)
    private val getEventByIdUseCase: GetEventByIdUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun configurar() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            HomeViewModel(
                getMapEventsUseCase,
                getSuggestedEventsUseCase,
                createEventUseCase,
                navigationRepository,
                getEventByIdUseCase,
            )
    }

    // onSearchQueryChange actualiza correctamente el texto buscado

    @Test
    fun `onSearchQueryChange_actualiza_currentQuery`() =
        runTest {
            viewModel.onSearchQueryChange("Hola")

            val valor = viewModel.searchUiState.value.currentQuery
            assertEquals("Hola", valor)
        }

    @Test
    fun `onSearchQueryChange_con_texto_vacio_coloca_estado_Idle`() =
        runTest {
            viewModel.onSearchQueryChange("")

            val estado = viewModel.searchUiState.value.searchState
            assertEquals(EventSearchState.Idle, estado)
        }
}
