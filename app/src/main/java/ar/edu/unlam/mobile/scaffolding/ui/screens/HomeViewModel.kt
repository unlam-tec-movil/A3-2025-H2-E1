package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventList
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUIState(
    val currentSearch: String? = "",
    var searchQuery: String? = "",
    val eventList: List<EventList> = emptyList(),
    val helloMessageState: MessageUIState,
)

@HiltViewModel
class HomeViewModel
    @Inject
    constructor() : ViewModel() {
        // Mutable State Flow contiene un objeto de estado mutable. Simplifica la operación de
        // actualización de información y de manejo de estados de una aplicación: Cargando, Error, Éxito
        // (https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
        // _helloMessage State es el estado del componente "HelloMessage" inicializado como "Cargando"
        private val helloMessage = MutableStateFlow(MessageUIState.Loading)

        // _Ui State es el estado general del view model.
        private val _uiState = MutableStateFlow(HomeUIState(helloMessageState = helloMessage.value))

        // UIState expone el estado anterior como un Flujo de Estado de solo lectura.
        // Esto impide que se pueda modificar el estado desde fuera del ViewModel.
        val uiState = _uiState.asStateFlow()

        init {
            _uiState.value =
                HomeUIState(
                    helloMessageState = MessageUIState.Success("2b"),
                )
        }

        fun onSearchQueryChange(newQuery: String) {
            _uiState.update { currentState ->
                currentState.copy(searchQuery = newQuery)
            }
        }

        fun onCancelSearch() {
            _uiState.update { currentState ->
                if (currentState.currentSearch.isNullOrEmpty()) {
                    currentState.copy(searchQuery = "")
                } else {
                    currentState.copy(searchQuery = _uiState.value.currentSearch)
                }
            }
        }

        fun onSearch(query: String) {
            _uiState.update { currentState ->
                currentState.copy(currentSearch = query)
            }
            // TODO Hacer la búsqueda de eventos
        }
    }
