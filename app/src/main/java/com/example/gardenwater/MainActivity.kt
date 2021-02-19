package com.example.gardenwater

import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.CurrentWeatherForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        val CORE_POOL_SIZE = 3
        val MAXIMUM_POOL_SIZE = 10
        val KEEP_ALIVE_TIME = 5000L
    }

    //  private lateinit var binding: ActivityMainBinding

    private lateinit var ilsaveCircle: CustomDropView

    private lateinit var guideline1: Guideline
    private lateinit var guideline2: Guideline

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAreas: RecyclerView

    private lateinit var imHose: ImageView

    private lateinit var tvTemperetureValue: TextView
    private lateinit var tvHumidity: TextView

    private lateinit var threadPoolExecutor: ThreadPoolExecutor

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("MainActivity", newConfig.orientation.toString())
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            guideline1.setGuidelinePercent(0.25f)
            guideline2.setGuidelinePercent(0.66f)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            guideline1.setGuidelinePercent(0.33f)
            guideline2.setGuidelinePercent(0.75f)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        //val view = binding.root
        setContentView(R.layout.activity_main)

        ilsaveCircle = findViewById(R.id.ilsaveCircle)
        guideline1 = findViewById(R.id.guideline1)
        guideline2 = findViewById(R.id.guideline2)
        tvTemperetureValue = findViewById(R.id.tvTemperatureValue)
        tvHumidity = findViewById(R.id.tvHumidityValue)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        ilsaveCircle.setOnClickListener {
            if (ilsaveCircle.tag != null && ilsaveCircle.tag == "focused") {
                Log.d("MainActivity", "you tapped on my view!")
                ilsaveCircle.focusedState = CustomDropView.PRESSED
                ilsaveCircle.text = "2"
                ilsaveCircle.tag = "pressed"
            } else {
                ilsaveCircle.focusedState = CustomDropView.FOCUSED
                ilsaveCircle.text = "1"
                ilsaveCircle.tag = "focused"
            }
            MyDialog().show(supportFragmentManager, "hey")
            ilsaveCircle.text = (3 - ilsaveCircle.text.toString().toInt()).toString()
        }


        threadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue()
        )

        threadPoolExecutor.submit(Runnable {
            val currentWeatherForecast = RetrofitClient.getCurrentWeather().execute().body()
            runOnUiThread {
                tvTemperetureValue.text = String.format(
                    resources
                        .getString(R.string.temp_value),
                    currentWeatherForecast?.weather?.temp.toString()
                )
                tvHumidity.text = String.format(
                    resources
                        .getString(R.string.humidity_value),
                    currentWeatherForecast?.weather?.temp.toString()
                )
            }

            var listWeather = RetrofitClient.getWeatherForecast().execute().body()?.daily
            Log.d("MainActivityWeather", listWeather.toString())
            for ((index, item) in listWeather!!.withIndex()) {
                var url = item.weatherImage[0].getIconUrl()

                var stream = RetrofitClient.getImage(url).execute().body()?.byteStream()

                var myBitmap = BitmapFactory.decodeStream(stream)
                listWeather[index].imageBitmap = myBitmap
            }




            runOnUiThread {
                recyclerView.adapter = AdapterWeather(listWeather)
                (recyclerView.adapter as AdapterWeather).notifyDataSetChanged()
            }
        }
        )

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = AdapterWeather(
//                listOf(
//                        Weather("February 8, 2020", 25, R.drawable.cloudy),
//                        Weather("February 9, 2020", 26, R.drawable.partly_cloudy),
//                        Weather("February 10, 2020", 27, R.drawable.rain)
//                )
//        )
//        (recyclerView.adapter as AdapterWeather).notifyDataSetChanged()


        recyclerViewAreas = findViewById(R.id.recyclerViewAreas)
        recyclerViewAreas.layoutManager = LinearLayoutManager(this)
        recyclerViewAreas.adapter = AdapterAreas(
            listOf(
                Area("BackYard", false),
                Area("BackPatio", false),
                Area("Front Yard", false),
                Area("Garden", false),
                Area("Porch", false)
            )
        )
        (recyclerViewAreas.adapter as AdapterAreas).notifyDataSetChanged()

        imHose = findViewById(R.id.ivHose)
        imHose.setOnClickListener {
            with(imHose) {
                if (tag != null && tag.toString() == "hoseOff") {
                    Toast.makeText(this@MainActivity, "Water turned on", Toast.LENGTH_LONG).show()
                    setImageResource(R.drawable.sprinkler_on)
                    contentDescription = "Sprinkler is on"
                    tag = "hoseOn"
                } else {
                    Toast.makeText(this@MainActivity, "Water turned off", Toast.LENGTH_SHORT).show()
                    setImageResource(R.drawable.sprinkler_off)
                    tag = "hoseOff"
                    contentDescription = "Sprinkler is off"
                    recyclerViewAreas.adapter = AdapterAreas(
                        listOf(
                            Area("BackYard", false),
                            Area("BackPatio", false),
                            Area("Front Yard", false),
                            Area("Garden", false),
                            Area("Porch", false)
                        )
                    )
                    (recyclerViewAreas.adapter as AdapterAreas).notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        threadPoolExecutor.shutdown()
    }

}
