package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.network.GraphHopperApi
import ar.edu.unlam.mobile.scaffolding.domain.navigation.model.Route
import ar.edu.unlam.mobile.scaffolding.domain.navigation.repositories.NavigationRepository
import ar.edu.unlam.mobile.scaffolding.utils.ErrorResponse
import ar.edu.unlam.mobile.scaffolding.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class NavigationRepositoryImpl
    @Inject
    constructor(
        private val api: GraphHopperApi,
    ) : NavigationRepository {
        override fun getRoute(
            startLat: Double,
            endLat: Double,
            startLon: Double,
            endLon: Double,
        ): Flow<Resource<Route>> =
            flow {
                try {
                    val response =
                        api.getRoute(
                            points = listOf("$startLat,$startLon", "$endLat,$endLon"),
                        )
                    if (response.isSuccessful) {
                        val data = response.body()?.paths?.firstOrNull()
                        if (data != null) {
                            emit(Resource.Success(data.toRoute()))
                        } else {
                            emit(Resource.Error(message = "No route found"))
                        }
                    } else {
                        val errorMessage =
                            try {
                                val errorBody = response.errorBody()?.string()
                                if (errorBody != null) {
                                    val gson = Gson()
                                    gson.fromJson(errorBody, ErrorResponse::class.java).message
                                } else {
                                    response.message()
                                }
                            } catch (e: Exception) {
                                response.message()
                            }
                        emit(Resource.Error(message = errorMessage))
                    }
                } catch (e: HttpException) {
                    val errorMessage =
                        try {
                            val errorBody = e.response()?.errorBody()?.string()
                            if (errorBody != null) {
                                val gson = Gson()
                                gson.fromJson(errorBody, ErrorResponse::class.java).message
                            } else {
                                e.message()
                            }
                        } catch (e: Exception) {
                            e.message
                        }
                    emit(Resource.Error(message = errorMessage.toString()))
                } catch (e: Exception) {
                    emit(Resource.Error(message = e.message.toString()))
                }
            }
    }
