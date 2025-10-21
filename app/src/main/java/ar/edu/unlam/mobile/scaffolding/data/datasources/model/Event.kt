package ar.edu.unlam.mobile.scaffolding.data.datasources.model

import com.google.android.gms.maps.model.LatLng
import java.util.Date

    data class Event(
        val imageUrl: String,
        val title: String,
        val location: String,
        val date: Date,
        val coordinates: LatLng? = null
    )