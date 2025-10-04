package com.kylecorry.trail_sense.plugin.sample.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.util.Log

abstract class PluginService<T : IInterface>(protected val serviceName: String) : Service() {

    abstract fun getBinder(): T

    private val binder = getBinder().asBinder()

    override fun onBind(intent: Intent?): IBinder {
        Log.d(serviceName, "Service bound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(serviceName, "Service unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(serviceName, "Service destroyed")
        super.onDestroy()
    }

}