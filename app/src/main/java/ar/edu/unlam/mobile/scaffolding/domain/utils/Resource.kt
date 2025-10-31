package ar.edu.unlam.mobile.scaffolding.domain.utils

sealed class Resource<out T> {
    data class Success<out T>(
        val data: T,
    ) : Resource<T>()

    data class Error<T>(
        val data: T? = null,
        val message: String,
    ) : Resource<T>()
}
