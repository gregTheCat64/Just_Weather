package ru.javacat.justweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.justweather.R
import ru.javacat.justweather.ui.view_models.ActivityViewModel
import ru.javacat.justweather.ui.view_models.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ActivityViewModel by viewModels()

    override fun onPause() {
        super.onPause()
        println("Activity Pause")
        //viewModel.saveCurrentPlace()
    }

    override fun onResume() {
        super.onResume()
        println("MAINactivity on RESUME")
        //viewModel.getCurrentPlace()
    }

    override fun onDestroy() {
        //viewModel.saveCurrentPlace()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {


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
}