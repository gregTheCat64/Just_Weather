package ru.javacat.justweather.domain.models.suggestModels

data class FoundLocation(
    val address: Address,
    val distance: Distance,
    val subtitle: Subtitle,
    val tags: List<String>,
    val title: Title,
    val uri: String
)