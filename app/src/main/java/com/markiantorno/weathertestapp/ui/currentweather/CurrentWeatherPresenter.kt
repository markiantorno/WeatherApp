package com.markiantorno.weathertestapp.ui.currentweather

import com.markiantorno.weathertestapp.BuildConfig
import com.markiantorno.weathertestapp.objects.WeatherStatus
import com.markiantorno.weathertestapp.services.CurrentWeatherService
import kotlinx.android.synthetic.main.temperature_display_view.*
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber

class CurrentWeatherPresenter(var currentWeatherService: CurrentWeatherService) {

    fun fetchWeather(cityName: String) {
        val call = currentWeatherService.getWeatherForCity(BuildConfig.WEATHER_API_KEY, "Toronto")
        call.enqueue(object : Callback<WeatherStatus> {
            override fun onResponse(call: Call<WeatherStatus>, response: retrofit2.Response<WeatherStatus>) {
                if (response.isSuccessful) {
                    val weatherStatus = response.body()

                } else {
                    Timber.e("fetchMealContextsById !isSuccessful : " + response.message())
                }
            }

            override fun onFailure(call: Call<WeatherStatus>, t: Throwable) {
                Timber.e("fetchMealContextsById onFailure : " + t.message)
            }
        })
    }

}