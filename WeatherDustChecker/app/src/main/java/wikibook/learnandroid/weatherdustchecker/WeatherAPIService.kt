package wikibook.learnandroid.weatherdustchecker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// (1)
interface WeatherAPIService {
    // (2)
    @GET("/data/2.5/weather")
    fun getWeatherStatusInfo(
        // (3)
        @Query("appid") appId: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String="metric"
        // (4)
    ) : Call<OpenWeatherAPIJSONResponseFromGSON>
}

// (5)
data class OpenWeatherAPIJSONResponseFromGSON(val main: Map<String, String>, val weather: List<Map<String, String>>)
