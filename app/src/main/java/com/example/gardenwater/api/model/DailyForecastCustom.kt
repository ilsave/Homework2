package com.example.gardenwater.api.model

import android.graphics.Bitmap

data class DailyForecastCustom(
    var bitmap: Bitmap,
    var dailyForecast: DailyForecast
)