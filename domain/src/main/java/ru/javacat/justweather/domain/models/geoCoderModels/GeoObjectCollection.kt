package ru.javacat.justweather.domain.models.geoCoderModels

data class GeoObjectCollection(
    val featureMember: List<FeatureMember>,
    val metaDataProperty: MetaDataPropertyX
)