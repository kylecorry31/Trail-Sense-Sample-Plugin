package com.kylecorry.trail_sense_dem.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense_dem.aidl.DEMService
import com.kylecorry.trail_sense_dem.aidl.ElevationResult
import com.kylecorry.trail_sense_dem.infrastructure.DEM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class DEMServiceImpl : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val binder = object : DEMService.Stub() {
        override fun getElevation(latitude: Double, longitude: Double): ElevationResult {
            return try {
                Log.d(TAG, "Getting elevation for lat: $latitude, lon: $longitude")
                
                // Validate coordinates
                if (latitude < -90 || latitude > 90) {
                    return ElevationResult.error("Invalid latitude: $latitude. Must be between -90 and 90.")
                }
                if (longitude < -180 || longitude > 180) {
                    return ElevationResult.error("Invalid longitude: $longitude. Must be between -180 and 180.")
                }
                
                val coordinate = Coordinate(latitude, longitude)
                
                // Use runBlocking since AIDL methods are synchronous
                val elevation = runBlocking {
                    DEM.getElevation(this@DEMServiceImpl, coordinate)
                }
                
                val elevationMeters = elevation.meters().distance
                Log.d(TAG, "Elevation result: ${elevationMeters}m")
                
                ElevationResult.success(elevationMeters)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting elevation", e)
                ElevationResult.error("Error getting elevation: ${e.message}")
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
        serviceScope.cancel()
        Log.d(TAG, "Service destroyed")
    }

    companion object {
        private const val TAG = "DEMService"
    }
}