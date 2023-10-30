package ru.javacat.justweather.domain.models.suggestModels

import ru.javacat.justweather.data.network.response_models.suggestModels.Result

data class SuggestLocationList(
    val results: List<Result>
)