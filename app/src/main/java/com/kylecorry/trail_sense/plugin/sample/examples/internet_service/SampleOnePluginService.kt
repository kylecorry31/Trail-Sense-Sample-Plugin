package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.sol.math.SolMath.roundPlaces
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.aidl.ISampleOnePluginService
import com.kylecorry.trail_sense.plugin.sample.service.PluginPermissions
import com.kylecorry.trail_sense.plugin.sample.service.PluginService
import kotlinx.coroutines.runBlocking

class SampleOnePluginService : PluginService<ISampleOnePluginService>("SampleOnePluginService") {
    override fun getBinder(): ISampleOnePluginService {
        val context = this
        return object : ISampleOnePluginService.Stub() {
            override fun getWeather(
                latitude: Double,
                longitude: Double
            ): String? {
                // TODO: This is where permissions would be enforced (likely not needed since the intent of the plugins is to expand the abilities of Trail Sense)
//                PluginPermissions.enforcePermissions(context, Manifest.permission.INTERNET)
                // TODO: This is where the signatures of the release APKs would be added
                PluginPermissions.enforceSignature(context)

                val proxy = OpenMeteoProxy(context)
                val weather =
                    runBlocking {
                        proxy.getWeather(
                            Coordinate(
                                latitude.roundPlaces(2),
                                longitude.roundPlaces(2)
                            )
                        )
                    } ?: return null
                return JsonConvert.toJson(weather)
            }
        }
    }
}