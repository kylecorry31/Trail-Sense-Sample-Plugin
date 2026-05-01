package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

data class TileLayerPayload(
    val x: Int,
    val y: Int,
    val z: Int,
    val time: Long
)

data class FeatureLayerPayload(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
    val zoom: Int,
    val time: Long
)
