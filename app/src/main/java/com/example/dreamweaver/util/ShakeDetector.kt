package com.example.dreamweaver.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

/**
 * NOVEL FEATURE OF THIS PROJECT
 * ------------------------------
 * Listens to the device's accelerometer and fires [onShake] whenever a
 * sudden, shake-like motion is detected. Used to surprise the user with a
 * random dream-writing prompt while composing a new journal entry — a
 * sensor-based interaction that has not been used in any previous project.
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTimestamp = 0L
    private val shakeThreshold = 2.2f // measured in multiples of Earth's gravity
    private val minIntervalMs = 1200L // debounce so one shake = one prompt

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val gX = event.values[0] / SensorManager.GRAVITY_EARTH
        val gY = event.values[1] / SensorManager.GRAVITY_EARTH
        val gZ = event.values[2] / SensorManager.GRAVITY_EARTH
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > shakeThreshold) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimestamp > minIntervalMs) {
                lastShakeTimestamp = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

/**
 * Activates a [ShakeDetector] for as long as the calling Composable is in
 * the composition (e.g. only while the "Add dream" sheet is open).
 */
@Composable
fun ShakeListener(onShake: () -> Unit) {
    val context = LocalContext.current
    val latestOnShake by rememberUpdatedState(onShake)

    DisposableEffect(Unit) {
        val detector = ShakeDetector(context) { latestOnShake() }
        detector.start()
        onDispose { detector.stop() }
    }
}
