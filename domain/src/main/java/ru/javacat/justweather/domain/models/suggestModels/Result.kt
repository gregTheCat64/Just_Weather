package ru.javacat.justweather.domain.models.suggestModels

data class Result(
    val distance: Distance,
    val subtitle: Subtitle?,
    val tags: List<String>,
    val title: Title
)