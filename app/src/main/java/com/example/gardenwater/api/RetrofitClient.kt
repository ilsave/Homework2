package com.example.gardenwater.api

import com.example.gardenwater.api.model.CurrentWeatherForecast
import com.example.gardenwater.api.model.WeatherForecast
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.openweathermap.org"

object RetrofitClient {
    private val api: RetrofitService by lazy {
        getClient().create(RetrofitService::class.java)
    }

    private fun getClient(baseUrl: String = BASE_URL): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    suspend fun getWeatherForecast(): Response<WeatherForecast> {
        return api.getWeatherForecast()
    }

    suspend fun getCurrentWeather(): Response<CurrentWeatherForecast> {
        return api.getCurrentWeatherForecast()
    }

    suspend fun getImage(imageCode: String): Response<ResponseBody> {
        return api.getWeatherImage(imageCode)
    }
}