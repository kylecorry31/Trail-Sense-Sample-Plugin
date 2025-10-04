package com.kylecorry.trail_sense.plugin.sample.service

import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.aidl.ISampleOnePluginService
import com.kylecorry.trail_sense.plugin.sample.infrastructure.api.OpenMeteoProxy
import kotlinx.coroutines.runBlocking

class SampleOnePluginService : PluginService<ISampleOnePluginService>("SampleOnePluginService") {
    override fun getBinder(): ISampleOnePluginService {
        return object : ISampleOnePluginService.Stub() {
            override fun getWeather(
                latitude: Double,
                longitude: Double
            ): String? {
                val proxy = OpenMeteoProxy(this@SampleOnePluginService)
                val weather =
                    runBlocking { proxy.getWeather(Coordinate(latitude, longitude)) } ?: return null
                return JsonConvert.toJson(weather)
            }
        }
    }
}