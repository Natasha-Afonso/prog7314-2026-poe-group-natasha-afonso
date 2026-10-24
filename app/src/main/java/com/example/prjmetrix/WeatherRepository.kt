package com.example.prjmetrix



import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

data class WeatherSnapshot(
    val tempC: Int,
    val minC: Int,
    val maxC: Int,
    val rainChance: Int,
    val code: Int,
    val usedDefaultLocation: Boolean
) {
    val isRainy: Boolean
        get() = rainChance >= 50 || code in 51..67 || code in 80..82 || code in 95..99
    val isSnowy: Boolean
        get() = code in 71..77 || code in 85..86

    val emoji: String
        get() = when {
            code in 95..99 -> "⛈️"
            isSnowy -> "❄️"
            code in 51..67 || code in 80..82 -> "🌧️"
            code in 45..48 -> "🌫️"
            code == 0 -> if (tempC >= 28) "🔥" else "☀️"
            code in 1..3 -> "⛅"
            else -> "🌤️"
        }

    val label: String
        get() = when {
            code in 95..99 -> "Thunderstorms"
            isSnowy -> "Snow"
            code in 51..67 || code in 80..82 -> "Rain"
            code in 45..48 -> "Foggy"
            code == 0 -> "Clear"
            code in 1..3 -> "Partly cloudy"
            else -> "Fair"
        }

    val summary: String get() = "$emoji $tempC°C · $label"
}

object LocationHelper {
    //Used only when the phone has no recent location, or permission was denied
    const val DEFAULT_LAT = -26.2041
    const val DEFAULT_LON = 28.0473

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun lastKnown(context: Context): Pair<Double, Double>? {
        if (!hasPermission(context)) return null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val best = manager.getProviders(true)
            .mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        return best?.let { it.latitude to it.longitude }
    }
}

class WeatherRepository(private val context: Context) {

    suspend fun today(): Result<WeatherSnapshot> {
        return try {
            val location = LocationHelper.lastKnown(context)
            val lat = location?.first ?: LocationHelper.DEFAULT_LAT
            val lon = location?.second ?: LocationHelper.DEFAULT_LON

            val response = ExternalApis.weather.forecast(
                latitude = lat,
                longitude = lon,
                current = "temperature_2m,weather_code",
                daily = "temperature_2m_max,temperature_2m_min,precipitation_probability_max",
                timezone = "auto",
                forecastDays = 1
            )
            val body = response.body()
            val current = body?.current
            if (!response.isSuccessful || current?.temperature == null) {
                return Result.failure(Exception("Could not load today's weather."))
            }

            val temp = current.temperature.roundToInt()
            Result.success(
                WeatherSnapshot(
                    tempC = temp,
                    minC = body.daily?.min?.firstOrNull()?.roundToInt() ?: temp,
                    maxC = body.daily?.max?.firstOrNull()?.roundToInt() ?: temp,
                    rainChance = body.daily?.rainChance?.firstOrNull() ?: 0,
                    code = current.weatherCode ?: 0,
                    usedDefaultLocation = location == null
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Could not load today's weather. Check your connection."))
        }
    }
}
