package ru.javacat.justweather.data.network.response_models.suggestModels

data class Result(
    val address: Address,
    val distance: Distance,
    val subtitle: Subtitle,
    val tags: List<String>,
    val title: Title,
    val uri: String
)