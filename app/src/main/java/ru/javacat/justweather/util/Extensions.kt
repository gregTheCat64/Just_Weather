package ru.javacat.justweather.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

    fun String.toWindRus(): String{
        return this.replace(Regex("[NSWE]")){
            when (it.value) {
                "N" -> "С"
                "S"-> "Ю"
                "W"->"З"
                "E"->"В"
                else -> it.value
            }
        }
    }