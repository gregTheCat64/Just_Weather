package ru.javacat.ui

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   // private val viewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Life", "Activity Create, savedInstState: $savedInstanceState")
        super.onCreate(savedInstanceState)
        //viewModel.getCurrentPlace()
        //setTheme(R.style.Base_Theme_RainWeather)
        setContentView(R.layout.activity_main)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .add(R.id.fragmentContainer, StartFragment.newInstance())
//                .commit()
//        }


    }

    override fun onPause() {
        super.onPause()
        Log.i("Life", "Activity onPause")

        //viewModel.setMinimized(true)
    }

    override fun onResume() {
        super.onResume()
        Log.i("Life", "Activity onResume")
//        val appMinimized = viewModel.appMinimized.value
//        if (appMinimized == true){
//            viewModel.getCurrentPlace()
//        }

    }

    override fun onDestroy() {
        //viewModel.saveCurrentPlace()
        Log.i("Life", "Activity OnDestroy")
        //viewModel.setMinimized(false)
        super.onDestroy()
    }

}