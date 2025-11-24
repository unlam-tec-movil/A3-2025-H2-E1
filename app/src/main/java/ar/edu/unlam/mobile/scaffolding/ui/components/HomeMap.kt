package ar.edu.unlam.mobile.scaffolding.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import ar.edu.unlam.mobile.scaffolding.domain.event.model.SuggestedEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.library.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NearbyMap(
    nearbyEvents: List<SuggestedEvent>,
    modifier: Modifier = Modifier,
    route: List<GeoPoint>? = null,
    onEventoClick: (SuggestedEvent) -> Unit = {}, //  callback al hacer clic en un evento
    onLongPress: (GeoPoint?) -> Unit = {},
    mapProperties: MapProperties,
    rotationChanged: (Float) -> Unit = {},
    userLocation: GeoPoint?,
) {
    val context = LocalContext.current
    var initMap by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        initMap = true
    }

    if (userLocation != null || initMap) {
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
                    controller.setCenter(userLocation ?: GeoPoint(-34.6037, -58.3816))

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

                    // Eventos con click
                    val eventsReceiver =
                        object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false

                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                p?.let(onLongPress)
                                return true
                            }
                        }
                    val eventsOverlay = MapEventsOverlay(eventsReceiver)
                    overlays.add(eventsOverlay)

                    tag =
                        mapOf(
                            "gesture" to rotationGesture,
                            "events" to eventsOverlay,
                        )
                }
            },
            update = { mv ->
                val tagMap = mv.tag as Map<*, *>
                val rotationGestureOverlay = tagMap["gesture"] as RotationGestureOverlay
                val eventsOverlay = tagMap["events"] as MapEventsOverlay

                // Habilita la rotación por gestos
                rotationGestureOverlay.isEnabled = mapProperties.rotationByGesture

                // La rotacion por sensor esta en la screen y cuando la recives la rotacion del celu
                // la invierte para que el mapa apunte a la direccion de la brujula correctamente
                if (mapProperties.rotationBySensor) {
                    mv.mapOrientation = mapProperties.mapOrientation
                }

                if (mapProperties.enableLongPress) {
                    if (!mv.overlays.contains(eventsOverlay)) {
                        mv.overlays.add(eventsOverlay)
                    }
                } else {
                    mv.overlays.remove(eventsOverlay)
                }

                mv.overlays.removeAll { it is Marker }

                mapProperties.longPressPoint?.let { point ->
                    val longPressMarker =
                        Marker(mv).apply {
                            position = GeoPoint(point.latitude, point.longitude)
                            title = "Punto seleccionado"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                            icon?.setTint(Color.GREEN)

                            setOnMarkerClickListener { _, _ ->
                                onLongPress(null)
                                true
                            }
                        }
                    mv.overlays.add(longPressMarker)
                }

                //  Tu ubicación
                userLocation?.let {
                    val myMarker =
                        Marker(mv).apply {
                            position = GeoPoint(it.latitude, it.longitude)
                            title = "Estás aquí"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                            icon?.setTint(Color.BLUE)

                            // Esto lo puse para simular que si tocas tu posicion el punto verde desaparezca
                            // y asi fuera como que seleccionaste tu ubicacion
                            setOnMarkerClickListener { _, _ ->
                                onLongPress(null)
                                if (this.isInfoWindowShown) {
                                    this.closeInfoWindow()
                                } else {
                                    this.showInfoWindow()
                                }
                                true
                            }
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
                                onEventoClick(evento)
                                true // devuelve true para indicar que el evento fue manejado
                            }
                        }
                    mv.overlays.add(marker)
                }

                print("Dibujando ${nearbyEvents.size} eventos en el mapa.")

                if (mapProperties.targetLocation != null) {
                    val targetPoint = mapProperties.targetLocation
                    mv.controller.animateTo(targetPoint)
                }

                // Borrar rutas anteriores
                mv.overlays.removeAll { it is Polyline }

                // Navegación a evento
                if (route != null) {
                    val routePolyline =
                        Polyline().apply {
                            setPoints(route)
                            outlinePaint.color = Color.BLUE
                            outlinePaint.strokeWidth = 8f
                        }
                    val boundingBox = BoundingBox.fromGeoPoints(route)
                    mv.overlayManager.add(routePolyline)
                    mv.zoomToBoundingBox(boundingBox, true)
                }

                mv.invalidate()
            },
            onRelease = { mv ->
                mv.onDetach()
            },
        )
    }
}

data class MapProperties(
    val targetLocation: GeoPoint? = null,
    val zoom: Double = 15.0,
    val mapOrientation: Float = 0f,
    val rotationByGesture: Boolean = true,
    val rotationBySensor: Boolean = false,
    val longPressPoint: GeoPoint? = null,
    val enableLongPress: Boolean = true,
)
