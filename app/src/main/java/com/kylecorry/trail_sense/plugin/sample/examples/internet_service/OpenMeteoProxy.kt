package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.kylecorry.andromeda.files.CacheFileSystem
import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.luna.coroutines.onIO
import com.kylecorry.sol.science.meteorology.WeatherCondition
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.sol.units.Distance
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class CurrentWeatherDto(
    val time: String,
    val temperature_2m: Float,
    val relative_humidity_2m: Float,
    val weather_code: Int,
    val wind_speed_10m: Float,
)

class HourlyWeatherDto(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val relative_humidity_2m: List<Float>,
    val precipitation_probability: List<Float>,
    val rain: List<Float>,
    val showers: List<Float>,
    val snowfall: List<Float>,
    val weather_code: List<Int>,
    val wind_speed_10m: List<Float>
)

class DailyWeatherDto(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Float>,
    val temperature_2m_min: List<Float>,
    val precipitation_probability_max: List<Float>,
    val snowfall_sum: List<Float>,
    val showers_sum: List<Float>,
    val rain_sum: List<Float>
)

class ForecastDto(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double?,
    val utc_offset_seconds: Long,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto
)

class OpenMeteoProxy(context: Context) {

    private val cache = CacheFileSystem(context)
    private val http = HttpClient()

    private fun getUrl(location: Coordinate): String {
        val url = Uri.Builder()
            .scheme("https")
            .authority("api.open-meteo.com")
            .appendPath("v1")
            .appendPath("forecast")
            .appendQueryParameter("latitude", location.latitude.toString())
            .appendQueryParameter("longitude", location.longitude.toString())
            .appendQueryParameter(
                "daily",
                "weather_code,temperature_2m_max,temperature_2m_min,snowfall_sum,showers_sum,rain_sum,precipitation_probability_max"
            )
            .appendQueryParameter(
                "hourly",
                "temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,wind_speed_10m,rain,showers,snowfall"
            )
            .appendQueryParameter(
                "current",
                "temperature_2m,relative_humidity_2m,rain,showers,snowfall,weather_code,wind_speed_10m"
            )
            .appendQueryParameter("timezone", "auto")
            .build()

        return url.toString()
    }

    private suspend fun getForecast(location: Coordinate): ForecastDto? = onIO {
        // TODO: Better cache handling + lat lon cache
        // TODO: Parse json
        if (cache.getFile("weather.json", create = false).exists()) {
            val converted =
                JsonConvert.fromJson<ForecastDto>(cache.getFile("weather.json").readText())

            if (converted != null) {
                val timeSinceGeneration = Duration.between(
                    Instant.parse(converted.current.time + ":00.000Z")
                        .minusSeconds(converted.utc_offset_seconds),
                    Instant.now()
                )

                val distanceToGeneration =
                    Coordinate(converted.latitude, converted.longitude).distanceTo(location)

                // Recent / close enough to keep using
                if (timeSinceGeneration < Duration.ofHours(1) && distanceToGeneration < Distance.miles(
                        5f
                    ).meters().value
                ) {
                    Log.d("OpenMeteoProxy", "Using cached weather data")
                    return@onIO converted
                }
            }
        }

        val url = getUrl(location)
        val data = http.send(url).content?.toString(Charsets.UTF_8) ?: return@onIO null
        cache.getFile("weather.json").writeText(data)
        JsonConvert.fromJson<ForecastDto>(data)
    }

    suspend fun getWeather(location: Coordinate): TrailSenseForecast? = onIO {
        val forecast = getForecast(location) ?: return@onIO null
        val currentTime = Instant.parse("${forecast.current.time}:00.000Z")
            .minusSeconds(forecast.utc_offset_seconds)
        TrailSenseForecast(
            currentTime,
            forecast.elevation?.toFloat(),
            TrailSenseCurrentWeather(
                time = currentTime,
                temperature = forecast.current.temperature_2m,
                humidity = forecast.current.relative_humidity_2m / 100f,
                weather = mapWeatherCode(forecast.current.weather_code),
                windSpeed = forecast.current.wind_speed_10m,
            ),
            forecast.hourly.time.mapIndexed { index, time ->
                TrailSenseHourlyWeather(
                    time = Instant.parse("$time:00.000Z")
                        .minusSeconds(forecast.utc_offset_seconds),
                    temperature = forecast.hourly.temperature_2m[index],
                    humidity = forecast.hourly.relative_humidity_2m[index] / 100f,
                    precipitationChance = forecast.hourly.precipitation_probability[index] / 100f,
                    rainAmount = forecast.hourly.rain[index] + forecast.hourly.showers[index],
                    snowAmount = forecast.hourly.snowfall[index],
                    weather = mapWeatherCode(forecast.hourly.weather_code[index]),
                    windSpeed = forecast.hourly.wind_speed_10m[index]
                )
            }.sortedBy { it.time },
            forecast.daily.time.mapIndexed { index, time ->
                TrailSenseDailyWeather(
                    date = LocalDate.parse(time),
                    weather = mapWeatherCode(forecast.daily.weather_code[index]),
                    lowTemperature = forecast.daily.temperature_2m_max[index],
                    highTemperature = forecast.daily.temperature_2m_min[index],
                    precipitationChance = forecast.daily.precipitation_probability_max[index] / 100f,
                    snowAmount = forecast.daily.snowfall_sum[index],
                    rainAmount = forecast.daily.rain_sum[index] + forecast.daily.showers_sum[index]
                )
            }.sortedBy { it.date }
        )
    }

    private fun mapWeatherCode(code: Int): WeatherCondition? {
        return when (code) {
            0, 1, 2 -> WeatherCondition.Clear
            3, 45, 48 -> WeatherCondition.Overcast
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> WeatherCondition.Rain
            56, 57, 66, 67 -> WeatherCondition.Precipitation
            71, 73, 75, 77, 85, 86 -> WeatherCondition.Snow
            95, 96, 99 -> WeatherCondition.Thunderstorm
            else -> null
        }
    }

}