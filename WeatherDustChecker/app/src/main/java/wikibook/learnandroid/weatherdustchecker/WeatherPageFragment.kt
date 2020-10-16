package wikibook.learnandroid.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.net.URL
// import 구문 추가
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherPageFragment : Fragment() {
    // (1)
    @JsonIgnoreProperties(ignoreUnknown=true)
    data class OpenWeatherAPIJSONResponse(val main: Map<String, String>, val weather: List<Map<String, String>>)

    // (1)
    private val APP_ID = ""

    lateinit var weatherImage : ImageView;
    lateinit var statusText : TextView;
    lateinit var temperatureText : TextView;

    companion object {
        // (2)
        fun newInstance(lat: Double, lon: Double) : WeatherPageFragment {
            val fragment = WeatherPageFragment()

            val args = Bundle()
            // 번들 객체에 위치 정보를 추가
            args.putDouble("lat", lat)
            args.putDouble("lon", lon)
            fragment.setArguments(args)

            return fragment
        }
    }

    fun startAnimation() {
        // (1)
        val fadeIn = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        // (2)
        weatherImage.startAnimation(fadeIn)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.weather_page_fragment, container, false)

        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)
        statusText = view.findViewById<TextView>(R.id.weather_status_text)
        temperatureText = view.findViewById<TextView>(R.id.weather_temp_text)

        // (3)
        // 콜백 메서드를 통해 뷰 내용을 변경하는 작업을 진행할 것이므로 기존의 번들 객체에 접근하는 코드는 삭제
        /*
        weatherImage.setImageResource(arguments!!.getInt("res_id"))
        statusText.text = arguments!!.getString("status")
        temperatureText.text = "${arguments!!.getDouble("temperature")}°"
        */

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments!!.getDouble("lat")
        val lon = arguments!!.getDouble("lon")

        // (1)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // (2)
        val apiService = retrofit.create(WeatherAPIService::class.java)

        // (3)
        val apiCallForData = apiService.getWeatherStatusInfo(APP_ID, lat, lon)

        // (4)
        apiCallForData.enqueue(object : Callback<OpenWeatherAPIJSONResponseFromGSON> {
            override fun onFailure(call: Call<OpenWeatherAPIJSONResponseFromGSON>, t: Throwable) {
                // (5)
                Toast.makeText(activity, "에러 발생 : ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<OpenWeatherAPIJSONResponseFromGSON>, response: Response<OpenWeatherAPIJSONResponseFromGSON>) {
                // (6)
                val data = response.body()

                if(data != null) {
                    val temp = data.main.get("temp")
                    temperatureText.text = temp

                    val id = data.weather[0].get("id")
                    if(id != null) {
                        statusText.text = when {
                            id.startsWith("2") -> {
                                weatherImage.setImageResource(R.drawable.flash)
                                "천둥, 번개"
                            }
                            id.startsWith("3") -> {
                                weatherImage.setImageResource(R.drawable.rain)
                                "이슬비"
                            }
                            id.startsWith("5") -> {
                                weatherImage.setImageResource(R.drawable.rain)
                                "비"
                            }
                            id.startsWith("6") -> {
                                weatherImage.setImageResource(R.drawable.snow)
                                "눈"
                            }
                            id.startsWith("7") -> {
                                weatherImage.setImageResource(R.drawable.cloudy)
                                "흐림"
                            }
                            id.equals("800") -> {
                                weatherImage.setImageResource(R.drawable.sun)
                                "화창"
                            }
                            id.startsWith("8") -> {
                                weatherImage.setImageResource(R.drawable.cloud)
                                "구름 낌"
                            }
                            else -> "알 수 없음"
                        }
                    }
                }
            }
        })
    }
}
