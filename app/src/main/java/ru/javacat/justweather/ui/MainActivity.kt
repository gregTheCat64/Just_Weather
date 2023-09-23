package ru.javacat.justweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.justweather.R
import ru.javacat.justweather.ui.view_models.ActivityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Life", "Activity Create, savedInstState: $savedInstanceState")
        super.onCreate(savedInstanceState)
        //viewModel.getCurrentPlace()
        //setTheme(R.style.Base_Theme_RainWeather)
        setContentView(R.layout.activity_main)


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
        //viewModel.saveCurrentPlace()
        viewModel.setMinimized(true)
    }

    override fun onResume() {
        super.onResume()
        Log.i("Life", "Activity onResume")
        val appMinimized = viewModel.appMinimized.value
        if (appMinimized == true){
            viewModel.getCurrentPlace()
        }

    }

    override fun onDestroy() {
        //viewModel.saveCurrentPlace()
        Log.i("Life", "Activity OnDestroy")
        viewModel.setMinimized(false)
        super.onDestroy()
    }

}