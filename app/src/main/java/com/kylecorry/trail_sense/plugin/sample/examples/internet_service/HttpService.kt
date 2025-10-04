package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import com.kylecorry.luna.coroutines.onIO
import java.net.URL

class HttpService {
    suspend fun get(url: String): String = onIO {
        val url = URL(url)
        url.readText()
    }
}