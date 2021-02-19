package com.example.gardenwater.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gardenwater.R
import com.example.gardenwater.api.model.DailyForecastCustom

class AdapterWeather(
        var itemList: List<DailyForecastCustom>
): RecyclerView.Adapter<AdapterWeather.ItemsHolder>() {


    inner class  ItemsHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent,false)
        return ItemsHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ItemsHolder, position: Int) {
        val curItem = itemList[position]
        holder.itemView.apply {

            findViewById<TextView>(R.id.tvTemperature).text =
                String.format(resources.getString(R.string.temp_value), curItem.dailyForecast?.temp?.day.toString())

            findViewById<ImageView>(R.id.imvWeatherPic).setImageBitmap(curItem.bitmap)

            findViewById<TextView>(R.id.tvWeatherDate).text = curItem.dailyForecast?.getDate()
        }
    }
}