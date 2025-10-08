package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import com.kylecorry.sol.science.meteorology.WeatherCondition
import java.time.Instant
import java.time.LocalDate


data class TrailSenseForecast(
    val time: Instant,
    val elevation: Float?,
    val current: TrailSenseCurrentWeather,
    val hourly: List<TrailSenseHourlyWeather>,
    val daily: List<TrailSenseDailyWeather>
)

data class TrailSenseCurrentWeather(
    val time: Instant,
    val weather: WeatherCondition?,
    val temperature: Float?,
    val humidity: Float?,
    val windSpeed: Float?
)

data class TrailSenseHourlyWeather(
    val time: Instant,
    val weather: WeatherCondition?,
    val temperature: Float?,
    val humidity: Float?,
    val windSpeed: Float?,
    val precipitationChance: Float?,
    val rainAmount: Float?,
    val snowAmount: Float?
)

// TODO: Is this needed or can Trail Sense figure this out from the hourly data?
data class TrailSenseDailyWeather(
    val date: LocalDate,
    val weather: WeatherCondition?,
    val lowTemperature: Float?,
    val highTemperature: Float?,
    val precipitationChance: Float?,
    val rainAmount: Float?,
    val snowAmount: Float?
)

