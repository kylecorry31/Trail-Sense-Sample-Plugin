// IDigitalElevationModelService.aidl
package com.kylecorry.trail_sense.plugin.sample.aidl;

interface IDigitalElevationModelService {
    /**
     * Get elevation for a given latitude and longitude
     * @param latitude The latitude in decimal degrees
     * @param longitude The longitude in decimal degrees
     * @return the elevation in meters or NaN if there was an error
     */
    float getElevation(double latitude, double longitude);
}