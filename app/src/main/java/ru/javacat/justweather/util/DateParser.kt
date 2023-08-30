package ru.javacat.justweather.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.asDayOfWeek(): String = format(DateTimeFormatter.ofPattern("EEE"))

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.asLocalDate(): String = format(DateTimeFormatter.ofPattern("dd MMMM"))


@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDateTime(): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalTime(): LocalTime =
    LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.asHour(): String = format(DateTimeFormatter.ofPattern("h a"))

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.asTime(): String = format(DateTimeFormatter.ofPattern("hh:mm"))