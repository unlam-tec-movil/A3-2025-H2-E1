package ar.edu.unlam.mobile.scaffolding.data.datasources.network

import ar.edu.unlam.mobile.scaffolding.BuildConfig
import ar.edu.unlam.mobile.scaffolding.data.datasources.network.response.GetRouteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GraphHopperApi {
    @GET("route")
    suspend fun getRoute(
        @Query("point") points: List<String>,
        @Query("instructions") instructions: Boolean = false,
        @Query("points_encoded") encoded: Boolean = false,
        @Query("elevation") elevation: Boolean = false,
        @Query("simplify") simplify: Boolean = false,
        @Query("key") apiKey: String = API_KEY,
    ): Response<GetRouteResponse>

    companion object {
        const val BASE_URL = "https://graphhopper.com/api/1/"
        const val API_KEY = BuildConfig.API_KEY
    }
}
