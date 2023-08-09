package ru.javacat.justweather.response_models

data class Condition(
    val code: Int,
    val icon: String,
    val text: String
)