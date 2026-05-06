package com.kylecorry.trail_sense.plugin.sample.service

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationRouter
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationService
import com.kylecorry.andromeda.json.fromJson
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.trail_sense.plugin.sample.models.MapFeatureLayerRequest
import com.kylecorry.trail_sense.plugin.sample.models.MapTileLayerRequest
import com.kylecorry.trail_sense.plugin.sample.models.RegistrationFeaturesResponse
import com.kylecorry.trail_sense.plugin.sample.models.RegistrationMapLayerResponse
import com.kylecorry.trail_sense.plugin.sample.models.RegistrationResponse
import java.io.ByteArrayOutputStream

// This service is what Trail Sense will bind to - it links to an Intent action in the AndroidManifest
class SamplePluginService : InterprocessCommunicationService() {

    private suspend fun getGeoJson(payload: MapFeatureLayerRequest): String {
        // Placeholder for your feature that returns a GeoJson response
        // The things that matter here: it returns a valid GeoJSON object
        val bounds = CoordinateBounds(payload.north, payload.east, payload.south, payload.west)
        val color = Color.BLUE
        return """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "properties": {
                    "name": "Test",
                    "color": $color,
                    "isClickable": true
                  },
                  "geometry": {
                    "type": "Point",
                    "coordinates": [
                      ${bounds.center.longitude},
                      ${bounds.center.latitude}
                    ]
                  }
                }
              ]
            }
        """.trimIndent()
    }

    private suspend fun getTile(payload: MapTileLayerRequest): ByteArray? {
        // Placeholder for your feature that returns a map tile response
        // The things that matter here: bitmap is 256x256, it is returned as a byte array in a format that BitmapFactory can read
        val bitmap = createBitmap(256, 256)
        bitmap.eraseColor(
            Color.rgb(
                payload.x.coerceIn(0, 255),
                payload.y.coerceIn(0, 255),
                payload.z
            )
        )
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    override val router: InterprocessCommunicationRouter
        get() = InterprocessCommunicationRouter(
            mapOf(
                // This needs to be called /registration and return the RegistrationResponse payload
                "/registration" to { context, payload ->
                    // No signature check required
                    success(
                        RegistrationResponse(
                            RegistrationFeaturesResponse(
                                // This is where you list out all of your endpoints
                                mapLayers = listOf(
                                    RegistrationMapLayerResponse(
                                        "/geojson",
                                        "Sample",
                                        "feature"
                                    ),
                                    RegistrationMapLayerResponse(
                                        "/tiles",
                                        "Sample Tile",
                                        "tile"
                                    )
                                )
                            )
                        )
                    )
                },
                // Define the endpoints you referenced in mapLayers here. Make sure the layerType matches
                "/geojson" to { context, request ->
                    // You can do signature / permissions enforcement here using the PluginPermissions object
                    val parsedPayload = request.payload?.fromJson<MapFeatureLayerRequest>()
                    parsedPayload?.let { success(getGeoJson(it)) } ?: badRequest()
                },
                "/tiles" to { context, request ->
                    // You can do signature / permissions enforcement here using the PluginPermissions object
                    val parsedPayload = request.payload?.fromJson<MapTileLayerRequest>()
                    parsedPayload?.let { success(getTile(it)) } ?: badRequest()
                }
            )
        )
}
