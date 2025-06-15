package com.kylecorry.trail_sense_dem.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense_dem.aidl.IDigitalElevationModelService
import com.kylecorry.trail_sense_dem.infrastructure.DEM
import kotlinx.coroutines.runBlocking

class DigitalElevationModelService : Service() {

    private val binder = object : IDigitalElevationModelService.Stub() {
        override fun getElevation(latitude: Double, longitude: Double): Float {
            return try {
                Log.d(TAG, "Getting elevation for lat: $latitude, lon: $longitude")

                // Validate coordinates
                if (latitude < -90 || latitude > 90) {
                    return Float.NaN
                }
                if (longitude < -180 || longitude > 180) {
                    return Float.NaN
                }

                val coordinate = Coordinate(latitude, longitude)

                // Use runBlocking since AIDL methods are synchronous
                val elevation = runBlocking {
                    DEM.getElevation(this@DigitalElevationModelService, coordinate)
                }

                val elevationMeters = elevation.meters().distance
                Log.d(TAG, "Elevation result: ${elevationMeters}m")

                elevationMeters
            } catch (e: Exception) {
                Log.e(TAG, "Error getting elevation", e)
                Float.NaN
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "Service bound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    companion object {
        private const val TAG = "IDEMService"
    }
}