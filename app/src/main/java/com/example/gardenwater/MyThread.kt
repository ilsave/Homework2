package com.example.gardenwater

import android.graphics.BitmapFactory
import android.util.Log
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.DailyForecastCustom
import java.lang.ref.WeakReference

class MyThread(mainActivity: MainActivity) : Thread(Runnable {
    val weakReference = WeakReference<MainActivity>(mainActivity)

    val weakActivity = weakReference.get()
    val currentWeatherForecast = RetrofitClient.getCurrentWeather().execute().body()

    val listWeather = RetrofitClient.getWeatherForecast().execute().body()?.daily

    val listHelper = ArrayList<DailyForecastCustom>()

    for ((index, item) in listWeather!!.withIndex()) {
        listHelper.add(DailyForecastCustom(null, null))
        listHelper[index].dailyForecast = item
        val url = item.weatherImage[0].getIconUrl()
        val stream = RetrofitClient.getImage(url).execute().body()?.byteStream()
        val myBitmap = BitmapFactory.decodeStream(stream)
        listHelper[index].bitmap = myBitmap
    }

    val tempvalue = currentWeatherForecast?.weather?.temp.toString()
    val humvalue = currentWeatherForecast?.weather?.humidity.toString()
    weakActivity?.updateUi(tempvalue, humvalue, listHelper)
})