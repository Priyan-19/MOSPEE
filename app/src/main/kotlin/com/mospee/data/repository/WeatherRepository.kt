package com.mospee.data.repository

import com.mospee.domain.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor() {

    suspend fun getWeather(lat: Double, lon: Double): WeatherInfo = withContext(Dispatchers.IO) {
        try {
            // Using Open-Meteo (Free, no key required)
            val url = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true")
            val connection = url.openConnection()
            val text = connection.getInputStream().bufferedReader().readText()
            val json = JSONObject(text)
            val current = json.getJSONObject("current_weather")
            
            val temp = current.getDouble("temperature").toInt()
            val code = current.getInt("weathercode")
            
            val description = when(code) {
                0 -> "Clear"
                1, 2, 3 -> "Partly Cloudy"
                45, 48 -> "Foggy"
                51, 53, 55 -> "Drizzle"
                61, 63, 65 -> "Rainy"
                71, 73, 75 -> "Snowy"
                80, 81, 82 -> "Showers"
                95, 96, 99 -> "Stormy"
                else -> "Overcast"
            }
            
            WeatherInfo(
                temperature = temp,
                description = description,
                iconCode = code
            )
        } catch (e: Exception) {
            e.printStackTrace()
            WeatherInfo(24, "Partly Cloudy", 1) // Fallback
        }
    }
}
