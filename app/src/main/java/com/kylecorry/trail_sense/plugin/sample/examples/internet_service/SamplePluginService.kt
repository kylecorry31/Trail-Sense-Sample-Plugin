package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationRouter
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationService
import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.andromeda.json.fromJson
import com.kylecorry.sol.science.geology.CoordinateBounds
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.service.PluginPermissions
import com.kylecorry.trail_sense.plugin.sample.service.badRequest
import com.kylecorry.trail_sense.plugin.sample.service.success
import java.io.ByteArrayOutputStream

class SamplePluginService : InterprocessCommunicationService() {

    private suspend fun getWeather(context: Context, payload: ByteArray?): TrailSenseForecast? {
        val payloadJson = payload?.toString(Charsets.UTF_8) ?: return null
        val parsed = JsonConvert.fromJson<WeatherRequest>(payloadJson) ?: return null
        val proxy = OpenMeteoProxy(context)
        val response =
            proxy.getWeather(Coordinate(parsed.latitude, parsed.longitude)) ?: return null
        return response
    }

    private suspend fun getGeoJson(payload: FeatureLayerPayload): String {
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

    private suspend fun getTile(payload: TileLayerPayload): ByteArray? {
        if (payload.x != 0) {
            return null
        }

        val bitmap = createBitmap(256, 256)
        bitmap.eraseColor(Color.BLUE)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    override val router: InterprocessCommunicationRouter
        get() = InterprocessCommunicationRouter(
            mapOf(
                "/ping" to { context, payload ->
                    PluginPermissions.enforceSignature(context)
                    success("Pong")
                },
                "/registration" to { context, payload ->
                    // No signature check required
                    success(
                        RegistrationResponse(
                            RegistrationFeaturesResponse(
                                weather = listOf("/weather"),
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
                "/weather" to { context, payload ->
                    PluginPermissions.enforceSignature(context)
                    success(getWeather(context, payload))
                },
                "/geojson" to { context, payload ->
                    val parsedPayload = payload?.fromJson<FeatureLayerPayload>()
                    parsedPayload?.let { success(getGeoJson(it)) } ?: badRequest()
                },
                "/tiles" to { context, payload ->
                    val parsedPayload = payload?.fromJson<TileLayerPayload>()
                    parsedPayload?.let { success(getTile(it)) } ?: badRequest()
                }
            )
        )
}
