package ru.javacat.justweather.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.asDayOfWeek(): String = format(DateTimeFormatter.ofPattern("EEEE"))

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))