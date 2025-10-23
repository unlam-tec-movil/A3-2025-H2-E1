package ar.edu.unlam.mobile.scaffolding.utils

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getAddressFromCoordinates(
    context: Context,
    lat: Double,
    lng: Double,
): String =
    try {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    cont.resume(formatAddress(address), null)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val address = addresses?.firstOrNull()
            formatAddress(address)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error obteniendo dirección"
    }

private fun formatAddress(address: android.location.Address?): String =
    if (address != null) {
        listOfNotNull(
            address.thoroughfare,
            address.subThoroughfare,
            address.locality,
            address.adminArea,
            address.countryName,
        ).joinToString(", ")
    } else {
        "Dirección no encontrada"
    }
