package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.event.usecases.GetSuggestedEventsUseCase
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.EventSearchState
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.SearchUIState
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EventSearchViewModel
    @Inject
    constructor(
        private val getAutocompleteEvent: GetSuggestedEventsUseCase,
    ) : ViewModel() {
        private val _searchUiState = MutableStateFlow(SearchUIState())
        val searchUiState = _searchUiState.asStateFlow()

        private var searchEventJob: Job? = null

        fun onSearchQueryChange(newQuery: String) {
            _searchUiState.update { currentState ->
                currentState.copy(currentQuery = newQuery)
            }
            if (newQuery.isEmpty()) {
                _searchUiState.update { currentState ->
                    currentState.copy(searchState = EventSearchState.Idle)
                }
            }
        }

        @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
        private fun onSearchQueryObserve() {
            if (searchEventJob?.isActive == true) return
            searchEventJob =
                _searchUiState
                    .map { it.currentQuery }
                    .distinctUntilChanged()
                    .debounce(timeoutMillis = 500L)
                    .filter { query -> query.isNotBlank() && query.length > 1 }
                    .mapLatest { query ->
                        Log.d("HomeViewModel", "getSuggestionSearch: $query")
                        _searchUiState.update { it.copy(searchState = EventSearchState.Loading) }

                        getAutocompleteEvent(query).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    _searchUiState.update {
                                        it.copy(
                                            searchState =
                                                EventSearchState.Success(
                                                    // Para usar en el componente EventSearchBar
                                                    currentQuery = query,
                                                    events = result.data,
                                                ),
                                            // Para usar en HomeScreen (en el mapa para ser mas exactos) o vm
                                            currentQuery = query,
                                            eventList = result.data,
                                        )
                                    }
                                }

                                is Resource.Error -> {
                                    _searchUiState.update {
                                        it.copy(searchState = EventSearchState.Error(result.message))
                                    }
                                    Log.e(
                                        "HomeViewModel",
                                        "Error al obtener sugerencias: ${result.message}",
                                    )
                                }
                            }
                        }
                    }.launchIn(viewModelScope)
        }

        fun onActiveChange(isActive: Boolean) {
            _searchUiState.update { it.copy(isExpanded = isActive) }
            when (isActive) {
                true -> onSearchQueryObserve()
                false -> {
                    if (_searchUiState.value.eventList.isEmpty()) {
                        _searchUiState.update { currentState ->
                            currentState.copy(
                                searchState = EventSearchState.Idle,
                                lastQuery = "",
                                currentQuery = "",
                            )
                        }
                    }
                    searchEventJob?.cancel()
                }
            }
        }

        fun onSearch(searchQuery: String) {
            searchEventJob?.cancel()
            _searchUiState.update {
                it.copy(
                    isExpanded = false,
                    lastQuery = searchQuery,
                    currentQuery = searchQuery,
                )
            }
        }
    }
