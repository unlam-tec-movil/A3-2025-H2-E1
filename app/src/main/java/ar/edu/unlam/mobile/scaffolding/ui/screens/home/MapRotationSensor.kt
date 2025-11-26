package ar.edu.unlam.mobile.scaffolding.ui.screens.home

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun MapRotationSensor(
    enabled: Boolean,
    sensorManager: SensorManager,
    currentOrientation: Float,
    onOrientationChanged: (Float) -> Unit,
) {
    DisposableEffect(enabled) {
        val rotationSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
                ?: return@DisposableEffect onDispose {}

        if (enabled) {
            val listener =
                object : SensorEventListener {
                    private var lastOrientation = currentOrientation
                    private val alpha = 0.1f

                    override fun onSensorChanged(event: SensorEvent?) {
                        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                            val rotationMatrix = FloatArray(9)
                            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(rotationMatrix, orientation)

                            // Convierte la orientación a grados
                            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                            // Esto cambia el eje de sentido horario a antihorario para que la brujula apunte
                            // bien el este y el oeste, pero da problemas al poner la app en vertical.
                            azimuth = (360 - azimuth) % 360

                            val diff = ((azimuth - lastOrientation + 540f) % 360f) - 180f
                            lastOrientation += alpha * diff

                            onOrientationChanged(lastOrientation)
                        }
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int,
                    ) {}
                }

            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            onDispose {}
        }
    }
}
