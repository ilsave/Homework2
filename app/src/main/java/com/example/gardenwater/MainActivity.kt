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
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.DailyForecastCustom
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.reflect.Executable

// here i tried to implement 2 way of work with threads^ (inner implementation and separated class)

class MainActivity : AppCompatActivity() {

    //  private lateinit var binding: ActivityMainBinding

    private lateinit var ilsaveCircle: CustomDropView

    private lateinit var guideline1: Guideline
    private lateinit var guideline2: Guideline

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAreas: RecyclerView

    private lateinit var imHose: ImageView

    private lateinit var tvTemperetureValue: TextView
    private lateinit var tvHumidity: TextView

    private lateinit var thread: Thread
    private lateinit var myThread: MyThread
    private val TAG = "MainActivity"

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
        thread = Thread(Runnable {
            try {
                if (!thread.isInterrupted) {
                    RetrofitClient.getCurrentWeather()
                        .enqueue(object : Callback<CurrentWeatherForecast> {
                            override fun onResponse(
                                call: Call<CurrentWeatherForecast>,
                                response: Response<CurrentWeatherForecast>
                            ) {
                                if (response.isSuccessful) {
                                    Log.d("MainActivity", response.body().toString())
                                    runOnUiThread {
                                        tvTemperetureValue.text = String.format(
                                            resources
                                                .getString(R.string.temp_value),
                                            response.body()?.weather?.temp.toString()
                                        )
                                        tvHumidity.text = String.format(
                                            resources
                                                .getString(R.string.humidity_value),
                                            response.body()?.weather?.temp.toString()
                                        )
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<CurrentWeatherForecast>,
                                t: Throwable
                            ) {
                                Log.d("MainActivity", t.stackTrace.toString())
                            }
                        })
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



                    runOnUiThread {
                        recyclerView.adapter = AdapterWeather(listHelper)
                        (recyclerView.adapter as AdapterWeather).notifyDataSetChanged()
                    }
                }
            }catch (e: Exception){
                Log.d(TAG, e.toString())
            }
        })
       // thread.start()

        myThread = MyThread(this)
        myThread.start()
    }

    override fun onStop() {
        super.onStop()
        thread.stop()
        myThread.stop()
    }

    override fun onResume() {
        super.onResume()
        thread.resume()
        myThread.resume()
    }

    fun updateUi(tvTemp: String, tvHum: String, list: List<DailyForecastCustom>){
        runOnUiThread {
            tvTemperetureValue.text = tvTemp
            tvHumidity.text = tvHum
            recyclerView.adapter = AdapterWeather(list)
            (recyclerView.adapter as AdapterWeather).notifyDataSetChanged()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        thread.interrupt()
        myThread.interrupt()
    }
}