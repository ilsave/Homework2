package com.example.gardenwater

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.WeatherForecast
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.awaitResponse
import io.reactivex.rxjava3.schedulers.Schedulers


class ViewModelGarden: ViewModel() {
    private var mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    private val mCompositeDisposable = CompositeDisposable()


    init {
        mCompositeDisposable.add(
        RetrofitClient
            .getWeatherForecast()
            .subscribeOn(Schedulers.io())
            .subscribe({info ->
                mWeatherDailyForecast = info.daily
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}