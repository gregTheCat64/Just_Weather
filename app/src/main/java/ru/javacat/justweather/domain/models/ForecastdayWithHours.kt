package ru.javacat.justweather.domain.models

import androidx.room.Embedded
import java.time.LocalDate

data class ForecastdayWithHours(
    val astro: Astro,
    val date: LocalDate,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>
)