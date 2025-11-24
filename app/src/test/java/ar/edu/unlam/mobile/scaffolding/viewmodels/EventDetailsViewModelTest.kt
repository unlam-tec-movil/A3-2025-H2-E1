package ar.edu.unlam.mobile.scaffolding.viewmodels

import androidx.lifecycle.SavedStateHandle
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.SessionManager
import ar.edu.unlam.mobile.scaffolding.domain.event.repositories.EventRepository
import ar.edu.unlam.mobile.scaffolding.domain.user.model.UserItem
import ar.edu.unlam.mobile.scaffolding.ui.screens.EventDetailsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventDetailsViewModelTest {
    private lateinit var repository: EventRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: EventDetailsViewModel

    @Before
    fun setUp() {
        repository = mockk()
        sessionManager = mockk()
        savedStateHandle = SavedStateHandle(mapOf("id" to "123"))
    }

    @Test
    fun `cuando la sesion es invalida se setea el error y se deja de cargar`() =
        runTest {
            // GIVEN
            every { sessionManager.getLoggedUserId() } returns -1L

            // WHEN
            viewModel = EventDetailsViewModel(repository, sessionManager, savedStateHandle)

            // THEN
            assertEquals("Sesión no válida", viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `al hacer click en un participante se abre el popup y se asigna el usuario`() =
        runTest {
            // GIVEN
            val user =
                UserItem(
                    id = 1,
                    name = "Juan Perez",
                    avatarUrl = null,
                    description = null,
                )

            every { sessionManager.getLoggedUserId() } returns -1L

            viewModel = EventDetailsViewModel(repository, sessionManager, savedStateHandle)

            // WHEN
            viewModel.onParticipantClick(user)

            // THEN
            assertEquals(user, viewModel.uiState.value.selectedParticipant)
            assertTrue(viewModel.uiState.value.showParticipantPopup)
        }

    @Test
    fun `al cerrar el popup se limpian el usuario seleccionado y el estado del popup`() =
        runTest {
            // GIVEN
            every { sessionManager.getLoggedUserId() } returns -1L

            viewModel = EventDetailsViewModel(repository, sessionManager, savedStateHandle)

            val usuario =
                UserItem(
                    id = 1,
                    name = "Test",
                    avatarUrl = null,
                    description = "mail@test",
                )

            // Simular selección
            viewModel.onParticipantClick(usuario)

            // Preverificación
            assertEquals(usuario, viewModel.uiState.value.selectedParticipant)
            assertTrue(viewModel.uiState.value.showParticipantPopup)

            // WHEN
            viewModel.dismissParticipantPopup()

            // THEN
            assertNull(viewModel.uiState.value.selectedParticipant)
            assertFalse(viewModel.uiState.value.showParticipantPopup)
        }
}
