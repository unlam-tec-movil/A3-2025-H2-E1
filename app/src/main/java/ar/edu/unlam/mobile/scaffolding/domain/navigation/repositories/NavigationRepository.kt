package ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories

import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Route
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NavigationRepository {
    fun getRoute(
        startLat: Double,
        endLat: Double,
        startLon: Double,
        endLon: Double,
    ): Flow<Resource<Route>>
}
