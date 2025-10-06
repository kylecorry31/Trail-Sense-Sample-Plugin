package com.kylecorry.trail_sense.plugin.sample.service

import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IpcRouter(
    // TODO: Serializable instead of ByteArray?
    private val routes: Map<String, suspend (context: Context, payload: ByteArray?) -> ByteArray?>,
    private val looper: Looper = Looper.getMainLooper()
) {

    private var messenger: Messenger? = null

    private val scope = CoroutineScope(Dispatchers.Default)

    private fun createHandler(context: Context): Handler {
        return object : Handler(looper) {
            private val applicationContext = context.applicationContext

            override fun handleMessage(msg: Message) {
                val data = msg.data
                val route = data.getString("route") ?: run {
                    super.handleMessage(msg)
                    return
                }

                val action = routes[route] ?: run {
                    super.handleMessage(msg)
                    return
                }

                val replyTo = msg.replyTo

                scope.launch {
                    val response = action(applicationContext, data.getByteArray("payload"))
                    if (response != null && replyTo != null) {
                        val reply = Message.obtain()
                        val bundle = reply.data
                        // TODO: Response codes (missing permission, invalid caller, error)
                        bundle.putInt("code", 200)
                        bundle.putString("route", route)
                        bundle.putByteArray("payload", response)
                        replyTo.send(reply)
                    }
                }
            }
        }
    }

    fun bind(context: Context): IBinder? {
        messenger = Messenger(createHandler(context))
        return messenger?.binder
    }

    fun unbind() {
        messenger = null
    }
}