package com.example.gardenwater.ui

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gardenwater.repositories.Repository
import com.example.gardenwater.api.model.CurrentWeather
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.DailyForecastCustom
import kotlinx.coroutines.*
import java.lang.Exception


class ViewModelGarden(val repository: Repository): ViewModel() {
    var mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    var mCurrentWeather: MutableLiveData<CurrentWeather> = MutableLiveData()
    val currentWeather: LiveData<CurrentWeather> = mCurrentWeather

    var mWeatherForecastCustom: MutableLiveData<List<DailyForecastCustom>> = MutableLiveData()
    val weatherForecastCustom: LiveData<List<DailyForecastCustom>> = mWeatherForecastCustom



    init {
        getCurrentWeather()
        getWeatherForecastCustom()
    }

    private fun getWeatherForecastCustom() = viewModelScope.launch {

        try {
            val listHelper = ArrayList<DailyForecastCustom>()
            val response = repository.getWeatherForecast()
            if(response.isSuccessful){
                response.body()?.let {
                    for ((index, item) in it.daily.withIndex()) {
                        listHelper.add(DailyForecastCustom(null, null))
                        listHelper[index].dailyForecast = item
                        val url = item.weatherImage[0].getIconUrl()
                        val stream = repository.getImageUrl(url)
                        if (stream.isSuccessful){
                            stream.body()?.let {
                                val myBitmap = BitmapFactory.decodeStream(it.byteStream())
                                listHelper[index].bitmap = myBitmap
                            }
                        }
                    }
                }
            }
            mWeatherForecastCustom.postValue(listHelper)
        } catch (e: Exception){
            Log.d("ViewModel", e.toString())
        }
    }



    private fun getCurrentWeather() = viewModelScope.launch {
        try{
            val response = repository.getCurrentWeather()
            if (response.isSuccessful){
                response.body()?.let { mCurrentWeather.value = it.weather }
            }
        } catch (e: Exception){
            Log.d("ViewModel", e.toString())
        }

    }
}