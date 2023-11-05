package ru.javacat.justweather.domain.models.suggestModels

data class SuggestLocationList(
    val results: List<FoundLocation>,
    val suggest_reqid: String
)