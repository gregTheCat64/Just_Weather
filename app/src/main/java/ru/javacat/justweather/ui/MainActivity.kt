package ru.javacat.justweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.javacat.justweather.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, StartFragment.newInstance())
                .commit()
        }


    }
}