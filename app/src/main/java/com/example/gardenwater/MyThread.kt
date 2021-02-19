package com.example.gardenwater

import android.graphics.BitmapFactory
import android.util.Log
import com.example.gardenwater.api.RetrofitClient
import java.lang.ref.WeakReference

class MyThread(mainActivity: MainActivity): Thread(Runnable {
    val weakReference = WeakReference<MainActivity>(mainActivity)

    val weakActivity = weakReference.get()

    weakActivity!!.addNewText("wqwq")


    val currentWeatherForecast = RetrofitClient.getCurrentWeather().execute().body()

    val listWeather = RetrofitClient.getWeatherForecast().execute().body()?.daily
    Log.d("MainActivityWeather", listWeather.toString())
    for ((index, item) in listWeather!!.withIndex()) {
        val url = item.weatherImage[0].getIconUrl()

        val stream = RetrofitClient.getImage(url).execute().body()?.byteStream()

        val myBitmap = BitmapFactory.decodeStream(stream)
        listWeather[index].imageBitmap = myBitmap
    }
    var tempvalue = currentWeatherForecast?.weather?.temp.toString()
    var humvalue = currentWeatherForecast?.weather?.humidity.toString()
    weakActivity.updateUi(tempvalue, humvalue, listWeather)
})