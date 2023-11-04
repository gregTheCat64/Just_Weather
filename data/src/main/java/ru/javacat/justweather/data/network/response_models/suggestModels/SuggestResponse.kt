package ru.javacat.justweather.data.network.response_models.suggestModels

import ru.javacat.justweather.domain.models.suggestModels.FoundLocation

data class SuggestResponse(
    val results: List<FoundLocation>
)