package com.example.datchiki2

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.datchiki2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var currentSensor: Sensor? = null
    private lateinit var dataSen: MainActivity
    var sensText: String = ""

    private val lightSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    private val rotationSensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }
    private val accelerometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.sensText = sensText

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        binding.sensorGroup.setOnCheckedChangeListener { _, checkedId ->
            unregisterSensor()
            when (checkedId) {
                R.id.l -> setSensor(lightSensor, R.string.sensorAbsentL)
                R.id.r -> setSensor(rotationSensor, R.string.sensorAbsentR)
                R.id.a -> setSensor(accelerometer, R.string.sensorAbsentA)
            }
        }

        setSensor(lightSensor, R.string.sensorAbsentL)
    }

    private fun setSensor(sensor: Sensor?, absentMsgResId: Int) {
        if (sensor == null) {
            Toast.makeText(this, getString(absentMsgResId), Toast.LENGTH_SHORT).show()
        } else {
            currentSensor = sensor
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        currentSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterSensor()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        sensText = when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> "Освещённость: ${event.values[0]} лк"
            Sensor.TYPE_ROTATION_VECTOR -> "Вращение:\nX=${event.values[0]}\nY=${event.values[1]}\nZ=${event.values[2]}"
            Sensor.TYPE_ACCELEROMETER -> "Акселерометр:\nX=${event.values[0]} м/c²\nY=${event.values[1]} м/c²\nZ=${event.values[2]} м/c²"
            else -> ""
        }

        binding.sensText = sensText
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
