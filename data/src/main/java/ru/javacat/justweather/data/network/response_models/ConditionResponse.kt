package ru.javacat.justweather.data.network.response_models

data class ConditionResponse(
    val code: Int,
    val icon: String,
    val text: String
)