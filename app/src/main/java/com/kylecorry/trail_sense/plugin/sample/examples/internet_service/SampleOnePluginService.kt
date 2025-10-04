package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import com.kylecorry.andromeda.core.system.Package
import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.sol.math.SolMath.roundPlaces
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.aidl.ISampleOnePluginService
import com.kylecorry.trail_sense.plugin.sample.permissions.getSelfSignatureSha256Fingerprints
import com.kylecorry.trail_sense.plugin.sample.permissions.requireCallerSignature
import com.kylecorry.trail_sense.plugin.sample.service.PluginService
import kotlinx.coroutines.runBlocking

class SampleOnePluginService : PluginService<ISampleOnePluginService>("SampleOnePluginService") {
    override fun getBinder(): ISampleOnePluginService {
        return object : ISampleOnePluginService.Stub() {
            override fun getWeather(
                latitude: Double,
                longitude: Double
            ): String? {
                // TODO: This is where permissions would be enforced (likely not needed since the intent of the plugins is to expand the abilities of Trail Sense)
//                requireCallerPermission(this@SampleOnePluginService, Manifest.permission.INTERNET)
                // TODO: This is where the signatures of the release APKs would be added
                requireCallerSignature(
                    this@SampleOnePluginService,
                    Package.getSelfSignatureSha256Fingerprints(this@SampleOnePluginService)
                )

                val proxy = OpenMeteoProxy(this@SampleOnePluginService)
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