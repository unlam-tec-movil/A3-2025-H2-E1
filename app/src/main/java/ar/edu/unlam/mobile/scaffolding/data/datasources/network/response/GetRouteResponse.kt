package ar.edu.unlam.mobile.scaffolding.data.datasources.network.response

import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Route

data class GetRouteResponse(
    val paths: List<RoutePath>,
)

data class RoutePath(
    val distance: Double,
    val time: Long,
    val points: RoutePoints,
) {
    fun toRoute(): Route =
        Route(
            distanceMeters = distance,
            durationMillis = time,
            coordinates =
                points.coordinates.map { (lon, lat) ->
                    lat to lon
                },
        )
}

data class RoutePoints(
    val coordinates: List<List<Double>>,
)
