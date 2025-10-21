package ar.edu.unlam.mobile.scaffolding.data.datasources.model

import java.util.Date

    data class Event(
        val imageUrl: String,
        val title: String,
        val location: String,
        val date: Date,
        val locationDistance: String? = null
    )