package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

data class RegistrationFeaturesResponse(
    // TODO: This would be the list of features that Trail Sense can detect and allow plugins to override
    val weather: List<String> = emptyList(),
    val mapLayers: List<RegistrationMapLayerResponse> = emptyList()
)

data class RegistrationResponse(
    val features: RegistrationFeaturesResponse
)

data class RegistrationMapLayerResponse(
    val endpoint: String,
    val name: String,
    val layerType: String,
    val attribution: RegistrationMapLayerAttributionResponse? = null,
    val description: String? = null,
    val minZoomLevel: Int? = null,
    val isTimeDependent: Boolean = false,
    val refreshInterval: Long? = null,
    val refreshBroadcasts: List<String> = emptyList(),
    val cacheKeys: List<String>? = null,
    val shouldMultiply: Boolean = false
)

data class RegistrationMapLayerAttributionResponse(
    val attribution: String,
    val longAttribution: String? = null,
    val alwaysShow: Boolean = false
)
