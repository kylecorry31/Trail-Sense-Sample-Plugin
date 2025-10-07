package com.kylecorry.trail_sense.plugin.sample.service

import com.kylecorry.andromeda.ipc.CODE_OK
import com.kylecorry.andromeda.ipc.InterprocessCommunicationResponse
import com.kylecorry.andromeda.json.toJsonBytes

fun success(payload: Any?): InterprocessCommunicationResponse {
    val bytes = when (payload) {
        null -> {
            null
        }

        is ByteArray -> {
            payload
        }

        is String -> {
            payload.toByteArray()
        }

        else -> {
            payload.toJsonBytes()
        }
    }
    return InterprocessCommunicationResponse(CODE_OK, bytes)
}