package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import android.content.Context
import com.kylecorry.andromeda.core.system.Package
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationRouter
import com.kylecorry.andromeda.ipc.server.InterprocessCommunicationService
import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.service.PluginPermissions
import com.kylecorry.trail_sense.plugin.sample.service.success

class SamplePluginService : InterprocessCommunicationService() {

    private suspend fun getWeather(context: Context, payload: ByteArray?): TrailSenseForecast? {
        val payloadJson = payload?.toString(Charsets.UTF_8) ?: return null
        val parsed = JsonConvert.fromJson<WeatherRequest>(payloadJson) ?: return null
        val proxy = OpenMeteoProxy(context)
        val response =
            proxy.getWeather(Coordinate(parsed.latitude, parsed.longitude)) ?: return null
        return response
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
                            "Sample",
                            Package.getVersionName(context),
                            RegistrationFeaturesResponse(
                                weather = listOf("/weather")
                            )
                        )
                    )
                },
                "/weather" to { context, payload ->
                    PluginPermissions.enforceSignature(context)
                    success(getWeather(context, payload))
                },
            )
        )
}