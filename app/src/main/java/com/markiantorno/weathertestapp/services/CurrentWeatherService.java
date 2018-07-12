package com.markiantorno.weathertestapp.services;

import com.markiantorno.weathertestapp.objects.WeatherStatus;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeatherService {

    String AUTH_HEADER = "APPID";
    String QUERY = "q";

    String CITY_WEATHER_URL = "data/2.5/weather";

    @GET(CITY_WEATHER_URL)
    Call<WeatherStatus> getWeatherForCity(@Query(AUTH_HEADER) String id, @Query(QUERY) String... params);

}
