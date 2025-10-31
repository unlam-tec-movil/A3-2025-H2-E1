package ar.edu.unlam.mobile.scaffolding.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

//  clase de datos para los eventos
data class Evento(
    val nombre: String,
    val lat: Double,
    val lon: Double,
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NearbyMap(
    nearbyEvents: List<Evento>,
    modifier: Modifier = Modifier,
    onEventoClick: (Evento) -> Unit = {}, //  callback al hacer clic en un evento
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (!permissionState.status.isGranted) {
        Text("Se necesita permiso de ubicación para mostrar el mapa")
        return
    }

    val currentLocation = remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)
        currentLocation.value = location?.let { GeoPoint(it.latitude, it.longitude) }
            ?: GeoPoint(-34.6037, -58.3816) // fallback: Obelisco
    }

    if (currentLocation.value == null) {
        Text("Esperando ubicación...")
        return
    }

    val mapView =
        remember {
            Configuration.getInstance().load(
                context,
                PreferenceManager.getDefaultSharedPreferences(context),
            )
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(currentLocation.value)
            }
        }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    AndroidView(factory = { mapView }, modifier = modifier.fillMaxSize()) { mv ->
        mv.overlays.clear()

        //  Tu ubicación
        val myMarker =
            Marker(mv).apply {
                position = currentLocation.value!!
                title = "Estás aquí"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                // esta linea muestra error en R  porque es un icono interno de OSMDroid
                icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)
                icon?.setTint(android.graphics.Color.BLUE)
            }
        mv.overlays.add(myMarker)

        //  Eventos con click
        nearbyEvents.forEach { evento ->
            val marker =
                Marker(mv).apply {
                    position = GeoPoint(evento.lat, evento.lon)
                    title = evento.nombre
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    // esta linea muestra un error en R  porque es un icono interno de OSMDroid
                    icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)
                    icon?.setTint(android.graphics.Color.RED)

                    // Acción al hacer clic
                    setOnMarkerClickListener { _, _ ->
                        onEventoClick(evento)
                        true // devuelve true para indicar que el evento fue manejado
                    }
                }
            mv.overlays.add(marker)
        }

        mv.invalidate()
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
}

// pantalla mapa: manejo del componente desde la implementacion
@Composable
fun MapScreen() {
    //  Lista de eventos en Buenos Aires
    val eventos =
        listOf(
            Evento("Evento 1 - Obelisco", -34.603722, -58.381592),
            Evento("Evento 2 - Casa Rosada", -34.608056, -58.370278),
            Evento("Evento 3 - Puerto Madero", -34.616389, -58.364167),
            Evento("Evento 4 - Recoleta", -34.588056, -58.397222),
            Evento("Evento 5 - Palermo", -34.571667, -58.423056),
        )

    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

    //  Mapa interactivo
    NearbyMap(
        nearbyEvents = eventos,
        onEventoClick = { evento ->
            eventoSeleccionado = evento
        },
    )

    //  Diálogo al tocar un evento
    eventoSeleccionado?.let { evento ->
        AlertDialog(
            onDismissRequest = { eventoSeleccionado = null },
            confirmButton = {
                Button(onClick = { eventoSeleccionado = null }) {
                    Text("Cerrar")
                }
            },
            title = { Text(evento.nombre) },
            text = { Text("Detalles próximamente...") },
        )
    }
}
