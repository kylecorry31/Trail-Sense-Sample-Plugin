package com.kylecorry.trail_sense.plugin.sample.infrastructure.api

import com.kylecorry.luna.coroutines.onIO
import java.net.URL

class HttpService {
    suspend fun get(url: String): String = onIO {
        val url = URL(url)
        url.readText()
    }
}