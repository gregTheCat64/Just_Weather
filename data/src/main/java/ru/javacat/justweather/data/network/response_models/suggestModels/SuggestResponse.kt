package ru.javacat.justweather.data.network.response_models.suggestModels

import ru.javacat.justweather.domain.models.suggestModels.Result

data class SuggestResponse(
    val results: List<Result>,
    val suggest_reqid: String
)