package ru.javacat.justweather.data.network.response_models.suggestModels

import ru.javacat.justweather.domain.models.suggestModels.Hl

data class Title(
    val hl: List<Hl>,
    val text: String
)