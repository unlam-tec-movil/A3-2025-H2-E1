package ar.edu.unlam.mobile.scaffolding.components

import ar.edu.unlam.mobile.scaffolding.domain.event.model.EventItem
import ar.edu.unlam.mobile.scaffolding.ui.components.getEventDateById
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class EventHomeCardUnitTest {
    @Test
    fun getEventDateById_devuelve_fecha_cuando_el_id_existe() {
        val list =
            listOf(
                EventItem(
                    id = "1",
                    title = "Test Event",
                    description = "Test description",
                    dateTime = 1000L,
                    lat = 0.0,
                    lng = 0.0,
                    image = null,
                ),
            )

        val result = getEventDateById(list, "1")

        assertEquals(1000L, result)
    }

    @Test
    fun getEventDateById_devuelve_null_cuando_el_id_no_existe() {
        val list =
            listOf(
                EventItem(
                    id = "1",
                    title = "Test Event",
                    description = "Test description",
                    dateTime = 1000L,
                    lat = 0.0,
                    lng = 0.0,
                    image = null,
                ),
            )

        val result = getEventDateById(list, "999")

        assertNull(result)
    }
}
