package com.markiantorno.weathertestapp.dependencyinjection

import com.markiantorno.weathertestapp.BuildConfig
import com.markiantorno.weathertestapp.services.CurrentWeatherService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class NetworkModule {

    private val lengthTimeoutConnect: Long = 5000
    private val lengthTimeoutRead: Long = 2000

    @Singleton
    @Provides
    fun provideCurrentWeatherService(retrofit: Retrofit): CurrentWeatherService {
        return retrofit.create<CurrentWeatherService>(CurrentWeatherService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.OPEN_WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(lengthTimeoutRead, TimeUnit.SECONDS)
                .connectTimeout(lengthTimeoutConnect, TimeUnit.SECONDS)
                .build()
    }
}