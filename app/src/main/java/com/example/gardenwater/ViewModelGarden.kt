package com.example.gardenwater

import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gardenwater.api.model.CurrentWeather
import com.example.gardenwater.api.model.DailyForecast
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*


class ViewModelGarden(val repository: Repository): ViewModel() {
    var mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    var mCurrentWeather: MutableLiveData<CurrentWeather> = MutableLiveData()
    val currentWeather: LiveData<CurrentWeather> = mCurrentWeather

    public fun getMList() = mWeatherDailyForecast


    init {
        getWeatherForecast()
        getCurrentWeather()
    }


    private fun getWeatherForecast() = viewModelScope.launch {
        val response = repository.getWeatherForecast()
        if(response.isSuccessful){
            response.body()?.let {
                for ((index, item) in it.daily.withIndex()) {
                    val url = item.weatherImage[0].getIconUrl()
                    val stream = repository.getImageUrl(url)
                    if (stream.isSuccessful){
                        stream.body()?.let {
                            val myBitmap = BitmapFactory.decodeStream(it.byteStream())
                            item.imageBitmap = myBitmap
                        }
                    }
                }
                mWeatherDailyForecast.postValue(it.daily)
            }
        }
    }

    private fun getCurrentWeather() = viewModelScope.launch {
        val response = repository.getCurrentWeather()
        if (response.isSuccessful){
            response.body()?.let { mCurrentWeather.value = it.weather }
        }
    }
}