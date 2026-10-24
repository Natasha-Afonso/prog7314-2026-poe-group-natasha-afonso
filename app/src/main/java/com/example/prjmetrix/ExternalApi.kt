package com.example.prjmetrix



import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

//APIs (weather, public holidays, background remover)

//Weather (Open-Meteo)
data class WeatherResponse(
    val current: CurrentWeather? = null,
    val daily: DailyWeather? = null
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double? = null,
    @SerializedName("weather_code") val weatherCode: Int? = null
)

data class DailyWeather(
    @SerializedName("temperature_2m_max") val max: List<Double?>? = null,
    @SerializedName("temperature_2m_min") val min: List<Double?>? = null,
    @SerializedName("precipitation_probability_max") val rainChance: List<Int?>? = null
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("daily") daily: String,
        @Query("timezone") timezone: String,
        @Query("forecast_days") forecastDays: Int
    ): Response<WeatherResponse>
}

//National holidays (Nager.Date)
data class PublicHoliday(
    val date: String = "",//e.g 2026-12-25
    val localName: String = "",
    val name: String = "",
    val countryCode: String = ""
)

interface HolidayApi {
    @GET("api/v3/PublicHolidays/{year}/{country}")
    suspend fun holidays(
        @Path("year") year: Int,
        @Path("country") country: String
    ): Response<List<PublicHoliday>>
}


object ExternalApis {

    private fun <T> build(baseUrl: String, service: Class<T>, timeoutSeconds: Long): T {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("X-Api-Key")
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }

    val weather: WeatherApi by lazy {
        build("https://api.open-meteo.com/", WeatherApi::class.java, 20)
    }

    val holidays: HolidayApi by lazy {
        build("https://date.nager.at/", HolidayApi::class.java, 20)
    }

}
