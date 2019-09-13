package net.unsweets.gamma.service

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import net.unsweets.gamma.domain.repository.PnutCacheRepository

class ClearStreamCacheService : IntentService("ClearStreamCacheService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        val cacheDir = PnutCacheRepository.getUserCacheDir(baseContext) ?: return
        cacheDir.deleteRecursively()
        val broadcast = Intent(action)
        sendBroadcast(broadcast)
    }

    class Receiver(val listener: Listener) : BroadcastReceiver() {
        interface Listener {
            fun onReceive()
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            listener.onReceive()
        }
    }


    companion object {
        @JvmStatic
        fun startService(context: Context?) {
            val intent = Intent(context, ClearStreamCacheService::class.java)
            context?.startService(intent)
        }

        private val action = ClearStreamCacheService::class.java.simpleName
        val intentFilter = IntentFilter(action)
    }
}
