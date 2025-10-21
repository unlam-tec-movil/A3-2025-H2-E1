package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.domain.model.EventList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable
sealed interface HelloMessageUIState {
    data class Success(
        val message: String,
    ) : HelloMessageUIState

    data object Loading : HelloMessageUIState

    data class Error(
        val message: String,
    ) : HelloMessageUIState
}

data class HomeUIState(
    var searchQuery: String? = "",
    val eventList: List<EventList> = emptyList(),
    val helloMessageState: HelloMessageUIState,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor() : ViewModel() {
        // Mutable State Flow contiene un objeto de estado mutable. Simplifica la operación de
        // actualización de información y de manejo de estados de una aplicación: Cargando, Error, Éxito
        // (https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
        // _helloMessage State es el estado del componente "HelloMessage" inicializado como "Cargando"
        private val helloMessage = MutableStateFlow(HelloMessageUIState.Loading)

        // _Ui State es el estado general del view model.
        private val _uiState = MutableStateFlow(HomeUIState(helloMessageState = helloMessage.value))

        // UIState expone el estado anterior como un Flujo de Estado de solo lectura.
        // Esto impide que se pueda modificar el estado desde fuera del ViewModel.
        val uiState = _uiState.asStateFlow()

        init {
            _uiState.value =
                HomeUIState(
                    helloMessageState = HelloMessageUIState.Success("2b")
                )
        }

        fun onSearchQueryChange(newQuery: String) {
            _uiState.update { currentState ->
                currentState.copy(searchQuery = newQuery)
            }
        }

        fun onSearch(query: String) {
            // TODO: Hacer la búsqueda
        }
    }
