package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ar.edu.unlam.mobile.scaffolding.ui.common.MessageUIState
import ar.edu.unlam.mobile.scaffolding.ui.components.EventSearchBar
import ar.edu.unlam.mobile.scaffolding.ui.components.Greeting

const val HOME_SCREEN_ROUTE = "home"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // La información que obtenemos desde el view model la consumimos a través de un estado de
    // "tres vías": Loading, Success y Error. Esto nos permite mostrar un estado de carga,
    // un estado de éxito y un mensaje de error.
    val uiState: HomeUIState by viewModel.uiState.collectAsState()
    val searchBarState: SearchUIState by viewModel.searchUiState.collectAsStateWithLifecycle()

    when (val helloState = uiState.helloMessageState) {
        is MessageUIState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        is MessageUIState.Success -> {
            Scaffold { paddingValues ->
                EventSearchBar(
                    searchUiState = searchBarState,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onSearch = { query ->
                        viewModel.onSearch(query)
                    },
                    onSuggestionSelected = { suggestion ->
                        viewModel.onEventSelected(suggestion)
                    },
                    onActiveChange = viewModel::onActiveChange,
                )
                Box(
                    modifier =
                        modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                ) {
                    // TODO mapa de eventos
                    // Column solo de test, eliminar para colocar el mapa
                    Column {
                        Greeting(helloState.message, modifier)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(searchBarState.currentQuery)
                    }
                }
            }
        }

        is MessageUIState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = "Error",
                        modifier =
                            Modifier
                                .size(100.dp)
                                .align(Alignment.CenterHorizontally),
                    )
                    Text(text = helloState.message)
                }
            }
        }
    }
}
