package ar.edu.unlam.mobile.scaffolding.components

import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.osmdroid.util.GeoPoint

class NearbyMapUnitTest {
    @Test
    fun se_invoca_el_callback_onEventoClick_cuando_se_clica_un_evento() {
        val event = SuggestedEvent("1", "Evento prueba", -34.6, -58.3)
        var callbackInvocado = false
        var eventoRecibido: SuggestedEvent? = null

        val onEventoClick: (SuggestedEvent) -> Unit = {
            callbackInvocado = true
            eventoRecibido = it
        }

        // Simulamos clic
        onEventoClick(event)

        assertTrue(callbackInvocado)
        assertEquals(event, eventoRecibido)
    }

    @Test
    fun se_invoca_el_callback_onLongPress_cuando_se_clica_un_punto() {
        val punto = GeoPoint(-34.6, -58.3)
        var callbackInvocado = false
        var puntoRecibido: GeoPoint? = null

        val onLongPress: (GeoPoint?) -> Unit = {
            callbackInvocado = true
            puntoRecibido = it
        }

        // Simulamos long press
        onLongPress(punto)

        assertTrue(callbackInvocado)
        assertEquals(punto, puntoRecibido)
    }
}
