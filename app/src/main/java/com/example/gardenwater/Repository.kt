package com.example.gardenwater

import com.example.gardenwater.api.RetrofitClient

class Repository() {

    suspend fun getCurrentWeather() = RetrofitClient.getCurrentWeather()


    suspend fun getWeatherForecast() = RetrofitClient.getWeatherForecast()

    suspend fun  getImageUrl(Imagecode: String) = RetrofitClient.getImage(Imagecode)
}