package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.SearchUIState
import ar.edu.unlam.mobile.scaffolding.ui.screens.home.state.EventSearchState
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventSearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSearchBarIsExpanded() {
        // Given
        var onActiveChange = false

        val stateExpected =
            SearchUIState(
                eventList = emptyList(),
                currentQuery = "",
                lastQuery = "",
                isExpanded = false,
                searchState = EventSearchState.Idle,
            )

        composeTestRule.setContent {
            EventSearchBar(
                searchUiState = stateExpected,
                onSearchQueryChange = {},
                onSearch = {},
                onSuggestionSelected = {},
                onActiveChange = { isActive ->
                    if (isActive) {
                        onActiveChange = true
                    }
                },
            )
        }

        // When
        composeTestRule.onNodeWithText("Buscar").performClick()

        // Then
        Assert.assertTrue(onActiveChange)
    }

    @Test
    fun testSearchBarOnSearchQueryChange() {
        // Given
        var querySent = ""
        val queryIntroducida = "Concierto de Rock"

        val stateExpected =
            SearchUIState(
                eventList = emptyList(),
                currentQuery = "",
                lastQuery = "",
                isExpanded = true,
                searchState = EventSearchState.Idle,
            )

        composeTestRule.setContent {
            EventSearchBar(
                searchUiState = stateExpected,
                onSearchQueryChange = { newQuery ->
                    querySent = newQuery
                },
                onSearch = {},
                onSuggestionSelected = {},
                onActiveChange = {},
            )
        }

        // When
        composeTestRule.onNodeWithText("Buscar").performTextInput(queryIntroducida)

        // Then
        Assert.assertEquals(queryIntroducida, querySent)
    }
}
