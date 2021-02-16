package com.example.gardenwater.api.model

data class Temperature(
        var day: Float,
        val min: Float,
        val max: Float,
        val night: Float,
        val eve: Float,
        val morn: Float
)
