package ru.javacat.justweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.justweather.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        //setTheme(R.style.Base_Theme_RainWeather)
        setContentView(R.layout.activity_main)


//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.fragmentContainer, StartFragment.newInstance())
//                .commit()
//        }


    }
}