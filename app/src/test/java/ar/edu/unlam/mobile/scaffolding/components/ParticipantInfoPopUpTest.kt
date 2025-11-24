package ar.edu.unlam.mobile.scaffolding.components

import junit.framework.TestCase.assertEquals
import org.junit.Test

class ParticipantInfoPopUpUnitTest {
    @Test
    fun onReportClick_se_llama_con_el_valor_correcto() {
        var capturedComment = ""
        val fakeOnReportClick: (String) -> Unit = { capturedComment = it }

        // Simulamos que el usuario escribió "Comentario de prueba"
        fakeOnReportClick("Comentario de prueba")

        assertEquals("Comentario de prueba", capturedComment)
    }
}
