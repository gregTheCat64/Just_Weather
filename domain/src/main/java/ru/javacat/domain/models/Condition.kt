package ru.javacat.justweather.domain.models

data class Condition(
    val code: Int,
    val icon: String,
    val text: String
)