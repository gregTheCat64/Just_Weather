package ru.javacat.justweather.response_models

data class Alert(
    val areas: String,
    val category: String,
    val certainty: String,
    val desc: String,
    val effective: String,
    val event: String,
    val expires: String,
    val headline: String,
    val instruction: String,
    val msgtype: String,
    val note: String,
    val severity: String,
    val urgency: String
)