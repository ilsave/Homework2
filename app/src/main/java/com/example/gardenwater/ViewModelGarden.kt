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

    private val mCompositeDisposable = CompositeDisposable()

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

                  //  val stream = async(Dispatchers.Default) {
                 //   coroutineScope(Dispatchers.IO) {  }
                  //  withContext(Dispatchers.Default){
                        val stream = repository.getImageUrl(url).execute().body()?.byteStream()
                   // }
                    //}
                    val myBitmap = BitmapFactory.decodeStream(stream)
                    it.daily[index].imageBitmap = myBitmap
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


    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }

}