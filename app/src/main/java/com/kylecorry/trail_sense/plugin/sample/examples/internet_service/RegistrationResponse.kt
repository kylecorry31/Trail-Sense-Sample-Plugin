package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

data class RegistrationFeaturesResponse(
    // TODO: This would be the list of features that Trail Sense can detect and allow plugins to override
    val weather: List<String> = emptyList(),
    val mapLayers: List<String> = emptyList()
)

data class RegistrationResponse(
    val name: String,
    val version: String,
    val features: RegistrationFeaturesResponse
)