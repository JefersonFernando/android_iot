package com.example.cepconsult

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private fun getCEP(entry_cep: String) {
        val cep = findViewById<TextView>(R.id.CEP)
        val state = findViewById<TextView>(R.id.state)
        val city = findViewById<TextView>(R.id.city)
        val neighborhood = findViewById<TextView>(R.id.neighboorhood)
        val street = findViewById<TextView>(R.id.street)
        val coordinates = findViewById<TextView>(R.id.coordinates)

        cep.text = "CEP:"
        state.text = "Estado:"
        city.text = "Cidade:"
        neighborhood.text = "Bairro:"
        street.text = "Rua:"
        coordinates.text = "Coordenadas:"

        val client = OkHttpClient()
        val url = "https://brasilapi.com.br/api/cep/v2/" + entry_cep
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("HTTP", "Erro na requisicao")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Erro na requisicao", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                val resposta = response.body?.string()

                if (resposta == null) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "CEP não encontrado", Toast.LENGTH_SHORT).show()
                    }
                    return
                } else {
                    Log.d("HTTP", resposta)

                    val json = JSONObject(resposta)

                    val errorsArray = json.optJSONArray("errors")
                    if (errorsArray != null) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, errorsArray
                                .getJSONObject(0)
                                .getString("message"), Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val recvdCep = json.optString("cep")
                    val recvdState = json.optString("state")
                    val recvdCity = json.optString("city")
                    val recvdNeighborhood = json.optString("neighborhood")
                    val recvdStreet = json.optString("street")

                    val recvdLocation = json.optJSONObject("location")
                    val recvdcoordinates = recvdLocation?.optJSONObject("coordinates")

                    val recvdLatitude = recvdcoordinates?.optString("latitude")
                    val recvdLongitude = recvdcoordinates?.optString("longitude")

                    Log.d("JSON", "CEP: $recvdCep")
                    Log.d("JSON", "Estado: $recvdState")
                    Log.d("JSON", "Cidade: $recvdCity")
                    Log.d("JSON", "Bairro: $recvdNeighborhood")
                    Log.d("JSON", "Rua: $recvdStreet")
                    Log.d("JSON", "Lat: $recvdLatitude | Long: $recvdLongitude")
                    runOnUiThread {
                        cep.text = "CEP: $recvdCep"
                        state.text = "Estado: $recvdState"
                        city.text = "Cidade: $recvdCity"
                        neighborhood.text = "Bairro: $recvdNeighborhood"
                        street.text = "Rua: $recvdStreet"
                        coordinates.text = "Coordenadas:$recvdLatitude $recvdLongitude"
                    }
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val text = "Buscando CEP..."
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.searchAddress)
            .setOnClickListener {
                val cep = findViewById<EditText>(R.id.PostalAddress).text.toString()
                getCEP(cep)
                toast.show()
            }
    }
}