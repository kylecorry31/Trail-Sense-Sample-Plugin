package com.kylecorry.trail_sense.plugin.sample.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

abstract class IpcService : Service() {
    abstract fun getRoutes(): Map<String, suspend (context: Context, payload: ByteArray?) -> ByteArray?>

    private val router = IpcRouter(getRoutes())

    override fun onBind(intent: Intent?): IBinder? {
        return router.bind(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        router.unbind()
        return super.onUnbind(intent)
    }
}