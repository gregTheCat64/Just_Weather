package ru.javacat.domain.models


import ru.javacat.justweather.domain.models.Astro
import ru.javacat.justweather.domain.models.Day
import ru.javacat.justweather.domain.models.Hour
import java.time.LocalDate

data class ForecastdayWithHours(
    val astro: Astro,
    val date: LocalDate,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>
)