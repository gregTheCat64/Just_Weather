package ru.javacat.justweather.data.network.response_models

import ru.javacat.justweather.domain.models.Astro
import ru.javacat.justweather.domain.models.Day
import ru.javacat.justweather.domain.models.Hour
import java.time.LocalDate

data class ForecastdayResponse(
    val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: List<HourResponse>
)