package com.example.gardenwater

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.*
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    //  private lateinit var binding: ActivityMainBinding

    private lateinit var ilsaveCircle: CustomDropView

    private lateinit var guideline1: Guideline
    private lateinit var guideline2: Guideline

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAreas: RecyclerView

    private lateinit var imHose: ImageView

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




//       binding.buttonNumber.setOnClickListener {
//            MyDialog().show(supportFragmentManager, "hey")
//           binding.buttonNumber.text = (3 - binding.buttonNumber.text.toString().toInt()).toString()
//        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AdapterWeather(
                listOf(
                        Weather("February 8, 2020", 25, R.drawable.cloudy),
                        Weather("February 9, 2020", 26, R.drawable.partly_cloudy),
                        Weather("February 10, 2020", 27, R.drawable.rain)
                )
        )
        (recyclerView.adapter as AdapterWeather).notifyDataSetChanged()


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
}