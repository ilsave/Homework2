package com.example.gardenwater

import android.graphics.BitmapFactory
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
    var mWeatherDailyForecast: MutableLiveData<List<DailyForecast>> = MutableLiveData()
    val weatherForecast: LiveData<List<DailyForecast>> = mWeatherDailyForecast

    private val mCompositeDisposable = CompositeDisposable()

    public fun getMList() = mWeatherDailyForecast

    init {
        mCompositeDisposable.add(
        RetrofitClient
            .getWeatherForecast()
            .subscribeOn(Schedulers.io())
            .subscribe { info ->
                for ((index, item) in info.daily!!.withIndex()) {
                    var url = item.weatherImage[0].getIconUrl()

                    var stream = RetrofitClient.getImage(url).execute().body()?.byteStream()

                    var myBitmap = BitmapFactory.decodeStream(stream)
                    info.daily[index].imageBitmap = myBitmap
                }

                mWeatherDailyForecast.postValue(info.daily)
            }
        )


    }



    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}