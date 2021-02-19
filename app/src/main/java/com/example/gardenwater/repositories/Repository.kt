package com.example.gardenwater.repositories

import com.example.gardenwater.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository() {

    suspend fun getCurrentWeather() =  RetrofitClient.getCurrentWeather()

    suspend fun getWeatherForecast() = RetrofitClient.getWeatherForecast()

    suspend fun  getImageUrl(Imagecode: String) = RetrofitClient.getImage(Imagecode)
}