package com.kylecorry.trail_sense.plugin.sample.models

data class MapFeatureLayerRequest(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
    val zoom: Int,
    val time: Long
)
