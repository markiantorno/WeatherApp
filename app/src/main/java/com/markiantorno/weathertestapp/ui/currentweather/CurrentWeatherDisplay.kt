package com.markiantorno.weathertestapp.ui.currentweather

import com.markiantorno.weathertestapp.objects.Main

interface CurrentWeatherDisplay {

    fun weatherLoaded(mainWeather: Main)

}