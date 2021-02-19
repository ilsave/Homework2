package com.example.gardenwater

import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.CurrentWeather
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.DailyForecastCustom
import com.example.gardenwater.api.model.WeatherForecast
import io.reactivex.rxjava3.disposables.CompositeDisposable
import retrofit2.awaitResponse
import io.reactivex.rxjava3.schedulers.Schedulers


class ViewModelGarden : ViewModel() {
    var mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    var mCurrentWeather: MutableLiveData<CurrentWeather> = MutableLiveData()
    val currentWeather: LiveData<CurrentWeather> = mCurrentWeather

    var mWeatherForecastCustom: MutableLiveData<List<DailyForecastCustom>> = MutableLiveData()
    val weatherForecastCustom: LiveData<List<DailyForecastCustom>> = mWeatherForecastCustom

    private val mCompositeDisposable = CompositeDisposable()

    init {
        mCompositeDisposable.add(
            RetrofitClient
                .getWeatherForecast()
                .subscribeOn(Schedulers.io())
                .subscribe { info ->
                    val listHelper = ArrayList<DailyForecastCustom>()
                    for ((index, item) in info.daily!!.withIndex()) {
                        listHelper.add(DailyForecastCustom(null, null))
                        listHelper[index].dailyForecast = item
                        val url = item.weatherImage[0].getIconUrl()
                        val stream = RetrofitClient.getImage(url).execute().body()?.byteStream()
                        val myBitmap = BitmapFactory.decodeStream(stream)
                        listHelper[index].bitmap = myBitmap
                    }
                    mWeatherForecastCustom.postValue(listHelper)
                }
        )

        mCompositeDisposable.add(
            RetrofitClient
                .getCurrentWeather()
                .subscribeOn(Schedulers.io())
                .subscribe { info ->
                    mCurrentWeather.postValue(info.weather)
                }
        )


    }


    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}