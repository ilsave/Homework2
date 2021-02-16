package com.example.gardenwater.api.model


import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class DailyForecast(
    val dt: Long,
    var temp: Temperature,
    @SerializedName("weather")
    val weatherImage: List<WeatherImage>,
    var imageBitmap: Bitmap? = null
) {
    fun getDate(): String = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).run {
            format(Date(dt * 1000L).apply { timeZone = TimeZone.getTimeZone("Europe/Moscow") })
    }
}
