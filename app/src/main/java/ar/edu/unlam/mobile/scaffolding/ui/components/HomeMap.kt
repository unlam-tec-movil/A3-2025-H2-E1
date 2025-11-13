package ar.edu.unlam.mobile.scaffolding.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import kotlin.math.abs

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NearbyMap(
    nearbyEvents: List<SuggestedEvent>,
    modifier: Modifier = Modifier,
    mapProperties: MapProperties,
    rotationChanged: (Float) -> Unit = {},
    onEventoClick: (SuggestedEvent) -> Unit = {}, //  callback al hacer clic en un evento
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (!permissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(
                    "Se necesita permiso de ubicación para usar el mapa.",
                    textAlign = TextAlign.Center,
                )
                Button(onClick = { permissionState.launchPermissionRequest() }) {
                    Text("Conceder Permiso")
                }
            }
        }
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

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            ObservableMapView(context).apply {
                Configuration.getInstance().load(
                    context,
                    PreferenceManager.getDefaultSharedPreferences(context),
                )
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(currentLocation.value)

                // Rotacion por gestos
                val rotationGesture = RotationGestureOverlay(this)
                overlays.add(rotationGesture)

                // Esto se usa para rotar la brujula (por gestos) enviando la orientacion actual del mapa
                onOrientationChange = { orientation ->
                    rotationChanged(orientation)
                }

                tag = mapOf("gesture" to rotationGesture)
            }
        },
        update = { mv ->
            val tagMap = mv.tag as? Map<*, *> ?: return@AndroidView
            val rotationGestureOverlay = tagMap["gesture"] as RotationGestureOverlay

            // Habilita la rotación por gestos
            rotationGestureOverlay.isEnabled = mapProperties.rotationByGesture

            // La rotacion por sensor esta en la screen y cuando la recives la rotacion del celu
            // la invierte para que el mapa apunte a la direccion de la brujula correctamente
            if (mapProperties.rotationBySensor) {
                mv.mapOrientation = -mapProperties.mapOrientation
            }

            mv.overlays.removeAll { it is Marker }

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
                        position = GeoPoint(evento.lat, evento.lng)
                        title = evento.title
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
        },
        onRelease = { mv ->
            mv.onDetach()
        },
    )
}

data class MapProperties(
    val mapOrientation: Float = 0f,
    val rotationByGesture: Boolean = true,
    val rotationBySensor: Boolean = false,
)

// Esto hay que moverlo, si puede ser al vm
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

// Esto basicamente es para obtener los cambios de orientacion del dispositivo.
// Es el "MapView", con su funcionalidad intacta, pero le añade la capacidad de
// notificar al exterior cuando su orientación cambia.
class ObservableMapView(
    context: Context,
) : MapView(context) {
    var onOrientationChange: ((Float) -> Unit)? = null

    override fun setMapOrientation(
        orientation: Float,
        force: Boolean,
    ) {
        val oldOrientation = mapOrientation
        super.setMapOrientation(orientation, force)
        if (abs(mapOrientation - oldOrientation) > 0.5f) {
            onOrientationChange?.invoke(mapOrientation)
        }
    }
}
