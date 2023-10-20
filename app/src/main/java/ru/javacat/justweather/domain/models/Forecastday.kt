package ru.javacat.justweather.domain.models

import java.time.LocalDate

data class Forecastday(
    val astro: Astro,
    val date: LocalDate,
    val date_epoch: Int,
    val day: Day,
    //val hour: List<Hour>
)