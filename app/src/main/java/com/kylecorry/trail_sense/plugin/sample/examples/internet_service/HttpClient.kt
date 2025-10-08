package com.kylecorry.trail_sense.plugin.sample.examples.internet_service

import com.kylecorry.andromeda.json.JsonConvert
import com.kylecorry.luna.coroutines.onIO
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration

class HttpResponse(
    val code: Int,
    val headers: Map<String, List<String>>,
    val content: ByteArray?
) {
    fun contentAsString(): String? {
        return content?.toString(Charsets.UTF_8)
    }

    inline fun <reified T> contentAsJson(): T? {
        val json = contentAsString() ?: return null
        return JsonConvert.fromJson<T>(json)
    }
}

class HttpClient {

    suspend fun send(
        url: String,
        method: String = "GET",
        body: ByteArray? = null,
        headers: Map<String, List<String>> = emptyMap(),
        readTimeout: Duration? = null,
        connectTimeout: Duration? = null
    ): HttpResponse = onIO {
        val url = URL(url)
        val connection = url.openConnection() as HttpURLConnection
        if (readTimeout != null) {
            connection.readTimeout = readTimeout.toMillis().toInt()
        }
        if (connectTimeout != null) {
            connection.connectTimeout = connectTimeout.toMillis().toInt()
        }
        for ((key, value) in headers) {
            connection.setRequestProperty(key, value.joinToString(","))
        }
        connection.requestMethod = method
        if (body != null) {
            connection.doOutput = true
            connection.outputStream.use {
                it.write(body)
            }
        }
        connection.connect()
        val bytes = connection.getInputStream().use { it.readBytes() }
        val responseHeaders = connection.headerFields
        val responseCode = connection.responseCode
        HttpResponse(responseCode, responseHeaders, bytes)
    }
}