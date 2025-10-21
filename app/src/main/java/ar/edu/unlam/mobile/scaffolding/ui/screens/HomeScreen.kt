package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
    val isSearchActive = remember { mutableStateOf(false) }

    when (val helloState = uiState.helloMessageState) {
        is HelloMessageUIState.Loading -> {
            // Loading
        }

        is HelloMessageUIState.Success -> {
            Scaffold { paddingValues ->
                EventSearchBar(
                    searchQuery = uiState.searchQuery ?: "",
                    onSearchQueryChange = { newQuery ->
                        viewModel.onSearchQueryChange(newQuery)
                    },
                    onSearch = { query ->
                        isSearchActive.value = false
                        viewModel.onSearch(query)
                    },
                    onActiveChange = { isActive ->
                        isSearchActive.value = isActive
                    },
                    isExpanded = isSearchActive.value,
                )
                Box(
                    modifier =
                        modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                ) {
                    Greeting(helloState.message, modifier)
                    Text("Prueba")
                }
            }
        }

        is HelloMessageUIState.Error -> {
            // Error
        }
    }
}
