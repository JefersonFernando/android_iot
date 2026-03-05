package com.example.temp_sensor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TemperatureService : Service() {
    companion object {
        private const val CHANNEL_ID = "canal_fg_service"
    }

    private var running = false
    private var workerThread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (running == false) {
            running = true
            createNotificationChannel()
            val notification = createNotification()
            startForeground(1, notification)

            workerThread = Thread {
                try {
                    while (true) {
                        val value = readSensor()
                        val intent = Intent("SENSOR_UPDATE").apply {
                            setPackage(packageName)
                            putExtra("sensor_value", value)
                        }
                        sendBroadcast(intent)

                        Thread.sleep(1000)
                    }
                } catch (e: InterruptedException) {
                    Log.d("TemperatureService", "Leitura interrompida.")
                }
            }.also { it.start() }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.d("TemperatureService", "onDestroy")
        running = false
        workerThread?.interrupt()
        workerThread = null

        stopForeground(true)

        super.onDestroy()
    }


    private fun readSensor(): String {
        return try {
            val process = Runtime.getRuntime().exec("/bin/sensehat_cli temp")
            val reader = process.inputStream.bufferedReader()

            reader.readText()
        } catch (e: Exception) {
            "Erro ao ler sensor"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "temperature_channel",
            "Temperature service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(this, "temperature_channel")
            .setContentTitle("Temperature service")
            .setContentText("Collecting temperature data")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

}