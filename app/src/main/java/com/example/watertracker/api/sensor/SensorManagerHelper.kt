package com.example.watertracker.api.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorManagerHelper(context: Context, private val onCounterUpdated: (Double) -> Unit) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var counter: Double = 0.0
    private var previousX: Float = 0f

    private val upperThreshold = -2.0f  // Верхний порог (отпускаем)
    private val lowerThreshold = -3.5f  // Нижний порог (срабатывание)

    private var wasBelowThreshold = false

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]

        if (x > upperThreshold && wasBelowThreshold) {
            counter = 0.05
            onCounterUpdated(counter)
            wasBelowThreshold = false
        } else if (x < lowerThreshold) {
            wasBelowThreshold = true
        }

        previousX = x
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}