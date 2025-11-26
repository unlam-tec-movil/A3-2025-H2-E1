package ar.edu.unlam.mobile.scaffolding.ui.model

import android.net.Uri
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime

data class EventDraft(
    val title: String = "",
    val description: String = "",
    val location: GeoPoint? = null,
    val dateTime: LocalDateTime? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val imagesUri: List<Uri> = emptyList(),
)
