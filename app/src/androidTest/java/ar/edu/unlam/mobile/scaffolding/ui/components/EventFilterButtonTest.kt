package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class EventFilterButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    var selectedOption = mutableStateOf(0)

    @Test
    fun distanceFilterIsColoredWhenSelected() {
        composeTestRule.setContent {
            EventFilterButton(
                selectedOption = selectedOption.value,
                onDistanceFilter = { selectedOption.value = 1 },
                onDateFilter = { selectedOption.value = 0 },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Ordenar eventos")
            .performClick()

        composeTestRule
            .onNodeWithTag("filter_Más cercanos")
            .assertExists()
            .performClick()

        composeTestRule
            .onNodeWithTag("filter_Más cercanos_selected")
            .assertExists()
    }
}
