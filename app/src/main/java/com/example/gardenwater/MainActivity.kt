package com.example.gardenwater

import android.app.Activity
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.MaskFilter
import android.nfc.Tag
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gardenwater.api.RetrofitClient
import com.example.gardenwater.api.model.CurrentWeatherForecast
import com.example.gardenwater.api.model.DailyForecast
import com.example.gardenwater.api.model.DailyForecastCustom
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

open class MainActivity : AppCompatActivity() {

    companion object {
        val CORE_POOL_SIZE = 3
        val MAXIMUM_POOL_SIZE = 10
        val KEEP_ALIVE_TIME = 5000L
        val TAG = "MainActivity"
    }

    //  private lateinit var binding: ActivityMainBinding

    private lateinit var ilsaveCircle: CustomDropView

    private lateinit var guideline1: Guideline
    private lateinit var guideline2: Guideline

    lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAreas: RecyclerView

    private lateinit var imHose: ImageView

    lateinit var tvTemperetureValue: TextView
    lateinit var tvHumidity: TextView

    private lateinit var thread: Thread

    var progressbar: ProgressBar? = null

    var asyncTaskCurrentWeather: AsyncMyRequestsCurrentWeather? = null
    var asyncTaskCurrentWeatherList: AsyncMyRequestsCurrentWeatherList? = null
    var threadPoolExecutor: ThreadPoolExecutor? = null

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
        progressbar = findViewById(R.id.progressBar)
        ilsaveCircle = findViewById(R.id.ilsaveCircle)
        guideline1 = findViewById(R.id.guideline1)
        guideline2 = findViewById(R.id.guideline2)
        tvTemperetureValue = findViewById(R.id.tvTemperatureValue)
        tvHumidity = findViewById(R.id.tvHumidityValue)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        threadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue()
        )

        asyncTaskCurrentWeather = AsyncMyRequestsCurrentWeather(this)
        asyncTaskCurrentWeatherList = AsyncMyRequestsCurrentWeatherList(this)
        asyncTaskCurrentWeather!!.executeOnExecutor(threadPoolExecutor)
        asyncTaskCurrentWeatherList!!.executeOnExecutor(threadPoolExecutor)



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
    }

    override fun onDestroy() {
        super.onDestroy()
        asyncTaskCurrentWeather!!.cancel(true)
        asyncTaskCurrentWeatherList!!.cancel(true)
        threadPoolExecutor!!.shutdown()
    }

    fun setProgressbar(){
        progressbar?.visibility = View.VISIBLE
    }
}

class AsyncMyRequestsCurrentWeather(val activity: MainActivity) : AsyncTask<Unit, Unit, CurrentWeatherForecast>() {

    val weakReference: WeakReference<MainActivity> = WeakReference(activity)
    private var weakActivity: MainActivity

    init {
        weakActivity = weakReference.get()!!
    }

    override fun onPreExecute() {
        super.onPreExecute()
        weakActivity.progressbar?.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: Unit?): CurrentWeatherForecast
         = RetrofitClient.getCurrentWeather().execute().body()!!


    override fun onPostExecute(result: CurrentWeatherForecast?) {
        super.onPostExecute(result)
        (weakActivity).apply {
            tvTemperetureValue.text = String.format(
                resources
                    .getString(R.string.temp_value),
                result?.weather?.temp.toString()
            )
            tvHumidity.text = String.format(
                resources
                    .getString(R.string.humidity_value),
                result?.weather?.humidity.toString()
            )
            progressbar?.visibility = View.INVISIBLE
        }
    }
}


class AsyncMyRequestsCurrentWeatherList(val activity: MainActivity) : AsyncTask<Unit, Unit, List<DailyForecastCustom>>() {

    val weakReference: WeakReference<MainActivity> = WeakReference(activity)
    private var weakActivity: MainActivity

    init {
        weakActivity = weakReference.get()!!
    }

    override fun onPreExecute() {
        super.onPreExecute()
        weakActivity.progressbar?.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: Unit?): List<DailyForecastCustom> {
        val listHelper = ArrayList<DailyForecastCustom>()
        try {
            val listWeather = RetrofitClient.getWeatherForecast().execute().body()?.daily

            for ((index, item) in listWeather!!.withIndex()) {
                listHelper.add(DailyForecastCustom(null, null))
                listHelper[index].dailyForecast = item
                val url = item.weatherImage[0].getIconUrl()
                val stream = RetrofitClient.getImage(url).execute().body()?.byteStream()
                val myBitmap = BitmapFactory.decodeStream(stream)
                listHelper[index].bitmap = myBitmap
            }
        }catch (e: Exception){
            Log.d("MainActivity", e.toString() )
        }
        return listHelper
    }

    override fun onPostExecute(result: List<DailyForecastCustom>?) {
        super.onPostExecute(result)
        with(weakActivity){
            recyclerView.adapter = result?.let { AdapterWeather(it) }
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }
}

