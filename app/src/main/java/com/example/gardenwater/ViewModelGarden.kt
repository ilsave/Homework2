package com.example.gardenwater

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.WeatherForecast
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.awaitResponse

class ViewModelGarden: ViewModel() {
    private val mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    private val mCompositeDisposable = CompositeDisposable()


    init {
        RetrofitClient.getCurrentWeather()
            .subscribe()
    }

}