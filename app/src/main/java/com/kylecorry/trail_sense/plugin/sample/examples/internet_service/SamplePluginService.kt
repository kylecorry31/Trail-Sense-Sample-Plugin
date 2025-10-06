package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import android.content.Context
import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.service.IpcService
import com.kylecorry.trail_sense.plugin.sample.service.PluginPermissions

class SamplePluginService : IpcService() {

    private suspend fun getWeather(context: Context, payload: ByteArray?): ByteArray? {
        val payloadJson = payload?.toString(Charsets.UTF_8) ?: return null
        val parsed = JsonConvert.fromJson<WeatherRequest>(payloadJson) ?: return null
        val proxy = OpenMeteoProxy(context)
        val response =
            proxy.getWeather(Coordinate(parsed.latitude, parsed.longitude)) ?: return null
        return JsonConvert.toJson(response).toByteArray()
    }

    override fun getRoutes(): Map<String, suspend (Context, ByteArray?) -> ByteArray?> {
        return mapOf(
            "/ping" to { context, payload ->
                PluginPermissions.enforceSignature(context)
                "Pong".toByteArray()
            },
            "/registration" to { context, payload ->
                // No signature check required
                JsonConvert.toJson(
                    RegistrationResponse(
                        "Sample",
                        weather = listOf("/weather")
                    )
                ).toByteArray()
            },
            "/weather" to { context, payload ->
                PluginPermissions.enforceSignature(context)
                getWeather(context, payload)
            },
        )
    }
}