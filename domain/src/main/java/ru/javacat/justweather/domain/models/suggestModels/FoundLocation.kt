package ru.javacat.justweather.domain.models.suggestModels

import ru.javacat.justweather.data.network.response_models.suggestModels.Address
import ru.javacat.justweather.data.network.response_models.suggestModels.Distance
import ru.javacat.justweather.data.network.response_models.suggestModels.Subtitle
import ru.javacat.justweather.data.network.response_models.suggestModels.Title

data class FoundLocation(
    val address: Address,
    val distance: Distance,
    val subtitle: Subtitle,
    val tags: List<String>,
    val title: Title,
    val uri: String
)