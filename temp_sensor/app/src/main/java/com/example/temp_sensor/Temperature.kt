package com.example.temp_sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Temperature : AppCompatActivity() {
    private lateinit var temperatureView : TextView

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val value = intent?.getStringExtra("sensor_value")
            val temperature = value?.split(" ")[1]?.toDouble()
            Log.v("receiver", "Received value $value")
            temperatureView.text = "$temperature°C"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_temperature)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        temperatureView = findViewById<TextView>(R.id.temp)

        registerReceiver(receiver, IntentFilter("SENSOR_UPDATE"),RECEIVER_NOT_EXPORTED)
        val intent = Intent(this, TemperatureService::class.java)
        startForegroundService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        val intent = Intent(this, TemperatureService::class.java)
        stopService(intent)
    }
}