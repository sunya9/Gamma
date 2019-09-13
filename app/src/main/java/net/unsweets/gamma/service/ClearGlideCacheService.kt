package net.unsweets.gamma.service

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import net.unsweets.gamma.presentation.util.GlideApp

class ClearGlideCacheService : IntentService("ClearGlideCacheService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        GlideApp.getPhotoCacheDir(baseContext)?.deleteRecursively()
        val broadcast = Intent(action)
        sendBroadcast(broadcast)
    }

    class Receiver(val listener: Listener) : BroadcastReceiver() {
        interface Listener {
            fun onClearGlideCache()
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            listener.onClearGlideCache()
        }
    }


    companion object {
        @JvmStatic
        fun startService(context: Context?) {
            val intent = Intent(context, ClearGlideCacheService::class.java)
            context?.startService(intent)
        }

        private val action = ClearGlideCacheService::class.java.simpleName
        val intentFilter = IntentFilter(action)
    }
}
