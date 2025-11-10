package ar.edu.unlam.mobile.scaffolding.domain.navigation.model

data class Route(
    val distanceMeters: Double,
    val durationMillis: Long,
    val coordinates: List<Pair<Double, Double>>,
)