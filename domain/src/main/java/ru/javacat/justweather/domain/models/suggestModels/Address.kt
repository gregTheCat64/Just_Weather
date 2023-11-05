package ru.javacat.justweather.domain.models.suggestModels

data class Address(
    val component: List<Component>,
    val formatted_address: String
)