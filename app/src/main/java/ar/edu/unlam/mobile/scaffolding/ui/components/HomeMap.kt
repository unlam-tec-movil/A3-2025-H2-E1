package ar.edu.unlam.mobile.scaffolding.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.library.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@Composable
fun NearbyMap(
    nearbyEvents: List<SuggestedEvent>,
    modifier: Modifier = Modifier,
    lat: Double? = null,
    lng: Double? = null,
    onEventoClick: (SuggestedEvent) -> Unit = {}, //  callback al hacer clic en un evento
    mapProperties: MapProperties,
    rotationChanged: (Float) -> Unit = {},
    onMapStateChanged: (GeoPoint, Double) -> Unit = { _, _ -> },
    userLocation: GeoPoint?,
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                Configuration.getInstance().load(
                    context,
                    PreferenceManager.getDefaultSharedPreferences(context),
                )
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(mapProperties.zoom)
                controller.setCenter(mapProperties.center)

                // Esto se usa para rotar la brujula (por gestos) enviando la orientacion actual del mapa
                addMapListener(
                    object : MapListener {
                        private var lastOrientation = 0f

                        override fun onScroll(event: ScrollEvent?): Boolean {
                            val current = mapOrientation
                            if (current != lastOrientation) {
                                rotationChanged(current)
                                lastOrientation = current
                            }
                            return false
                        }

                        override fun onZoom(event: ZoomEvent?) = false
                    },
                )

                // Rotacion por gestos
                val rotationGesture = RotationGestureOverlay(this)
                overlays.add(rotationGesture)

                tag = rotationGesture
            }
        },
        update = { mv ->
            val rotationGestureOverlay = mv.tag as RotationGestureOverlay

            // Habilita la rotación por gestos
            rotationGestureOverlay.isEnabled = mapProperties.rotationByGesture

            // La rotacion por sensor esta en la screen y cuando la recives la rotacion del celu
            // la invierte para que el mapa apunte a la direccion de la brujula correctamente
            if (mapProperties.rotationBySensor) {
                mv.mapOrientation = mapProperties.mapOrientation
            }

            if (mapProperties.centerRequest && userLocation != null) {
                mv.controller.animateTo(userLocation)
            }

            mv.overlays.removeAll { it is Marker }

            //  Tu ubicación
            userLocation?.let {
                val myMarker =
                    Marker(mv).apply {
                        position = GeoPoint(it.latitude, it.longitude)
                        title = "Estás aquí"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                        icon?.setTint(Color.BLUE)
                    }
                mv.overlays.add(myMarker)
            }

            //  Eventos con click
            nearbyEvents.forEach { evento ->
                val marker =
                    Marker(mv).apply {
                        position = GeoPoint(evento.lat, evento.lng)
                        title = evento.title
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                        icon?.setTint(Color.RED)

                        // Acción al hacer clic
                        setOnMarkerClickListener { _, _ ->
                            onMapStateChanged(
                                mv.mapCenter as GeoPoint,
                                mv.zoomLevelDouble,
                            )
                            onEventoClick(evento)
                            true // devuelve true para indicar que el evento fue manejado
                        }
                    }
                mv.overlays.add(marker)
            }

            print("Dibujando ${nearbyEvents.size} eventos en el mapa.")

            if (lat != null && lng != null) {
                print("Centrándose en coordenadas: $lat, $lng")
                val targetPoint = GeoPoint(lat, lng)
                mv.controller.animateTo(targetPoint)
                mv.controller.setZoom(15.0)
            }

            mv.invalidate()
        },
        onRelease = { mv ->
            mv.onDetach()
        },
    )
}

data class MapProperties(
    val center: GeoPoint = GeoPoint(-34.6037, -58.3816),
    val zoom: Double = 15.0,
    val mapOrientation: Float = 0f,
    val rotationByGesture: Boolean = true,
    val rotationBySensor: Boolean = false,
    val centerRequest: Boolean = false,
)
