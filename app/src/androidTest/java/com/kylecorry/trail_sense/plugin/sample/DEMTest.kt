package com.kylecorry.trail_sense.plugin.sample

import androidx.test.platform.app.InstrumentationRegistry
import com.kylecorry.sol.math.statistics.Statistics
import com.kylecorry.sol.units.Coordinate
import com.kylecorry.trail_sense.plugin.sample.infrastructure.DEM
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.math.absoluteValue

class DEMTest {
    @Test
    fun getElevation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // https://elevation.maplogs.com/
        val tests = listOf(
            Coordinate(41.988, -71.733) to 197f,
            Coordinate(32.146, -81.332) to 12f,
            Coordinate(45.425, 9.611) to 90f,
            Coordinate(33.599, -111.768) to 606f,
            Coordinate(47.339, 8.529) to 456f,
            Coordinate(5.314, 6.878) to 47f,
            Coordinate(42.079, -80.06) to 308f,
            Coordinate(35.168, -83.278) to 985f,
            Coordinate(-16.513, -68.129) to 3594f,
            Coordinate(45.339, 22.894) to 1615f,
            Coordinate(48.672, -113.804) to 1098f,
            Coordinate(41.809, -72.467) to 161f,
            Coordinate(30.926, 78.233) to 2472f,
            Coordinate(43.57, -96.503) to 410f,
            Coordinate(51.519, 0.040) to 1f,
            Coordinate(0.0, -121.0) to 0f
        )

        val errors = tests.map { test ->
            val actual = runBlocking { DEM.getElevation(context, test.first) }
            TestCase.assertEquals(test.second, actual.meters().distance, 60f)
            actual.meters().distance - test.second
        }

        val quantile50 = Statistics.quantile(errors.map { it.absoluteValue }, 0.5f)
        val quantile90 = Statistics.quantile(errors.map { it.absoluteValue }, 0.9f)

        println("50% quantile: $quantile50")
        println("90% quantile: $quantile90")

        TestCase.assertEquals(0f, quantile50, 15f)
        TestCase.assertEquals(0f, quantile90, 30f)
    }

}