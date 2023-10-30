package ru.javacat.justweather.data.network.response_models.suggestModels

data class Address(
    val component: List<Component>,
    val formatted_address: String
)